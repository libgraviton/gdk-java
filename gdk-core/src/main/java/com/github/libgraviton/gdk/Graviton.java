package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the base class used for Graviton API calls.
 *
 * @todo do proper implementation (EVO-7721)
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class Graviton {

    public static final MediaType CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * Defines the base url of the Graviton server
     */
    private String baseUrl;

    /**
     * The object mapper used to serialize / deserialize to / from JSON
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The service manager which is used
     */
    private ServiceManager serviceManager;

    /**
     * The http client for making http calls.
     */
    private OkHttpClient okHttp;

    /**
     * Constructor
     *
     * @param baseUrl The base url pointing to the Graviton server
     * @param serviceManager The service manager to use
     */
    public Graviton(String baseUrl, ServiceManager serviceManager) {
        this.baseUrl = baseUrl;
        this.serviceManager = serviceManager;
        this.okHttp = new OkHttpClient();
    }

    /**
     * Returns the service manager
     *
     * @return The service manager
     */
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public <BeanClass> BeanClass get(String url, Class<? extends BeanClass> beanClass) {
        try {
            return objectMapper.readValue(get(url), beanClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            return okHttp.newCall(request).execute().body().string();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean post(Object data) throws NoCorrespondingEndpointException {
        Endpoint endpoint = serviceManager.getEndpoint(data.getClass().getName());
        try {
            return post(endpoint.getSchemaUrl(), objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e){
            return false;
        }
    }

    public boolean post(String url, String data) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(CONTENT_TYPE, data))
                    .build();
            return okHttp.newCall(request).execute().isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean put(Object data) throws NoCorrespondingEndpointException {
        Endpoint endpoint = serviceManager.getEndpoint(data.getClass().getName());
        Map<String, String> params = new HashMap<>();
        params.put("id", extractId(data));
        try {
            return put(endpoint.getSchemaUrl(), objectMapper.writeValueAsString(data), params);
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public boolean put(String url, String data) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .put(RequestBody.create(CONTENT_TYPE, data))
                    .build();
            return okHttp.newCall(request).execute().isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean put(String url, String data, Map<String, String> params) {
        return put(applyParams(url, params), data);
    }

    public boolean delete(Object data) throws NoCorrespondingEndpointException {
        Endpoint endpoint = serviceManager.getEndpoint(data.getClass().getName());
        Map<String, String> params = new HashMap<>();
        params.put("id", extractId(data));
        return delete(endpoint.getSchemaUrl(), params);
    }

    public boolean delete(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();
            return okHttp.newCall(request).execute().isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean delete(String url, Map<String, String> params) {
        return delete(applyParams(url, params));
    }

    private String applyParams(String url, Map<String, String> params) {
        if (params.size() > 0) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                url = url.replaceAll("\\{" + param.getKey() + "\\}", param.getValue());
            }
        }
        return url;
    }

    /**
     * @todo: Replace this with a cleaner solution, not relying on reflection (e.g. Base Graviton Item class / interface).
     *
     * Extracts the id of a given Graviton resource.
     *
     * @param data The Graviton resource.
     *
     * @return The extracted id.
     */
    private String extractId(Object data) {
        Class<?> clazz = data.getClass();
        Field field;
        try {
            field = clazz.getField("id");
            return (String) field.get(data);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return "";
        }
    }

}
