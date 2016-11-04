package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger LOG = LoggerFactory.getLogger(Graviton.class);

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
     * The endpoint manager which is used
     */
    private EndpointManager endpointManager;

    /**
     * The http client for making http calls.
     */
    private OkHttpClient okHttp;

    /**
     * Constructor
     *
     * @param baseUrl The base url pointing to the Graviton server
     * @param endpointManager The endpoint manager to use
     */
    public Graviton(String baseUrl, EndpointManager endpointManager) {
        this.baseUrl = baseUrl;
        this.endpointManager = endpointManager;
        this.okHttp = new OkHttpClient();
    }

    /**
     * Returns the endpoint manager
     *
     * @return The endpoint manager
     */
    public EndpointManager getEndpointManager() {
        return endpointManager;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public GravitonResponse get(String url) throws CommunicationException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return doRequest("GET", url, request);
    }

    public GravitonResponse post(Object data) throws NoCorrespondingEndpointException, CommunicationException {
        Endpoint endpoint = endpointManager.getEndpoint(data.getClass().getName());
        return post(endpoint.getUrl(), serializeData(data));
    }

    public GravitonResponse post(String url, String data) throws CommunicationException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(CONTENT_TYPE, data))
                .build();
        return doRequest("POST", url, request);
    }

    public GravitonResponse put(Object data) throws NoCorrespondingEndpointException, CommunicationException {
        Endpoint endpoint = endpointManager.getEndpoint(data.getClass().getName());
        Map<String, String> params = new HashMap<>();
        params.put("id", extractId(data));
        return put(endpoint.getUrl(), serializeData(data), params);
    }

    public GravitonResponse put(String url, String data) throws CommunicationException {
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(CONTENT_TYPE, data))
                .build();
        return doRequest("PUT", url, request);
    }

    public GravitonResponse put(String url, String data, Map<String, String> params) throws CommunicationException {
        return put(applyParams(url, params), data);
    }

    public GravitonResponse delete(Object data) throws NoCorrespondingEndpointException, CommunicationException {
        Endpoint endpoint = endpointManager.getEndpoint(data.getClass().getName());
        Map<String, String> params = new HashMap<>();
        params.put("id", extractId(data));
        return delete(endpoint.getUrl(), params);
    }

    public GravitonResponse delete(String url) throws CommunicationException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        return doRequest("DELETE", url, request);
    }

    public GravitonResponse delete(String url, Map<String, String> params) throws CommunicationException {
        return delete(applyParams(url, params));
    }

    protected GravitonResponse doRequest(String requestMethod, String url, Request request) throws CommunicationException {
        LOG.info("Starting " + requestMethod + " to '" + url + "'...");
        Response response;
        try {
            response = okHttp.newCall(request).execute();
        } catch (IOException e) {
            throw new CommunicationException("Unable to execute " + requestMethod + " to '" + url + "'.", e);
        }

        GravitonResponse gravitonResponse = new GravitonResponse(response);
        try {
            gravitonResponse.setBody(response.body().string());
        } catch (IOException e) {
            throw new CommunicationException("Unable to fetch response body from " + response.request().method() + " to '" + response.request().url() + "'.", e);
        } finally {
            response.close();
        }

        if(response.isSuccessful()) {
            LOG.info("Successful " + requestMethod + " to '" + url + "'. Response was '" + response.code() +" - " + response.message() + "'.");
        } else {
            String message = "Failed " + requestMethod + " to '" + url + "'. Response was '" + response.code() +" - " + response.message() + "' with body '" + gravitonResponse.getBody() + "'.";
            // TODO do we only want to throw an exception or also have the fail logged for sure?
            LOG.warn(message);
            throw new CommunicationException(message);
        }
        return gravitonResponse;
    }

    private String serializeData(Object data) throws CommunicationException {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e){
            throw new CommunicationException("Unable to serialize class '" + data.getClass().getName() + "'.", e);
        }
    }

    private String composeRequestFailedMessage(String requestMethod, String url, int code, String message, String body) {
        return "Failed " + requestMethod + " to '" + url + "'. Response was '" + code +" - " + message + "' with body '" + body + "'.";
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

    protected void setOkHttp(OkHttpClient okHttp) {
        this.okHttp = okHttp;
    }
}
