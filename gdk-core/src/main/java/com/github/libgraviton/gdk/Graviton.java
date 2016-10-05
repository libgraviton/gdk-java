package com.github.libgraviton.gdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.exception.NoCorrespondingServiceException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the base class used for Graviton API calls.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class Graviton {

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
     * Constructor
     *
     * @param baseUrl The base url pointing to the Graviton server
     * @param serviceManager The service manager to use
     */
    public Graviton(String baseUrl, ServiceManager serviceManager) {
        this.baseUrl = baseUrl;
        this.serviceManager = serviceManager;
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
        try {
            return Unirest.get(url).asString().getBody();
        } catch (UnirestException e) {
            // @todo
            return null;
        }
    }

    public boolean post(Object data) throws NoCorrespondingServiceException {
        Service service = serviceManager.getService(data.getClass().getName());
        return post(service.getCollectionUrl(), data);

    }

    public boolean post(String url, Object data) {
        try {
            Unirest.post(url).body(data).asString();
        } catch (UnirestException e) {
            return false;
        }
        return true;
    }

    public boolean put(Object data) throws NoCorrespondingServiceException {
        Service service = serviceManager.getService(data.getClass().getName());
        Map<String, String> params = new HashMap<>();
        params.put("id", extractId(data));
        return put(service.getCollectionUrl(), data, params);
    }

    public boolean put(String url, Object data) {
        return put(url, data, new HashMap<String, String>());
    }

    public boolean put(String url, Object data, Map<String, String> params) {
        HttpRequestWithBody request = Unirest.put(url);
        addRequestParams(request, params);
        try {
            request.body(data).asString();
        } catch (UnirestException e) {
            return false;
        }
        return true;
    }

    public boolean delete(Object data) throws NoCorrespondingServiceException {
        Service service = serviceManager.getService(data.getClass().getName());
        Map<String, String> params = new HashMap<>();
        params.put("id", extractId(data));
        return delete(service.getCollectionUrl(), params);
    }

    public boolean delete(String url) {
        return delete(url, new HashMap<String, String>());
    }

    public boolean delete(String url, Map<String, String> params) {
        HttpRequest request = Unirest.put(url);
        addRequestParams(request, params);
        try {
            request.asString();
        } catch (UnirestException e) {
            return false;
        }
        return true;
    }

    private void addRequestParams(HttpRequest request, Map<String, String> params) {
        if (params.size() > 0) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                request.routeParam(param.getKey(), param.getValue());
            }
        }
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
