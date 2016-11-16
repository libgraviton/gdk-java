package com.github.libgraviton.gdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import com.github.libgraviton.gdk.exception.SerializationException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;

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

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public GravitonRequest.Builder request() {
        return new GravitonRequest.Builder(this);
    }

    public GravitonRequest.Builder head(String url) {
        return request().url(url).head();
    }

    public GravitonRequest.Builder head(Object resource) throws NoCorrespondingEndpointException {
        return head(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder head(String id, Class clazz) throws NoCorrespondingEndpointException {
        return head(endpointManager.getEndpoint(clazz.getName()).getUrl())
                .param("id", id);
    }

    public GravitonRequest.Builder get(String url) {
        return request().url(url).get();
    }

    public GravitonRequest.Builder get(Object resource) throws NoCorrespondingEndpointException {
        return get(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder get(String id, Class clazz) throws NoCorrespondingEndpointException {
        return get(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .param("id", id);
    }

    public GravitonRequest.Builder delete(String url) {
        return request().url(url).delete();
    }

    public GravitonRequest.Builder delete(Object resource) throws NoCorrespondingEndpointException {
        return delete(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder delete(String id, Class clazz) throws NoCorrespondingEndpointException {
        return delete(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .param("id", id);
    }

    public GravitonRequest.Builder put(Object resource) throws NoCorrespondingEndpointException, SerializationException {
        return request()
                .url(endpointManager.getEndpoint(resource.getClass().getName()).getItemUrl())
                .param("id", extractId(resource))
                .put(resource);
    }

    public GravitonRequest.Builder post(Object resource) throws NoCorrespondingEndpointException, SerializationException {
        return request()
                .url(endpointManager.getEndpoint(resource.getClass().getName()).getUrl())
                .put(resource);
    }

    public GravitonResponse execute(GravitonRequest request) throws CommunicationException {
        Request okhttpRequest = request.getOkhttpRequest();
        LOG.info(String.format("Starting '%s' to '%s'...", okhttpRequest.method(), okhttpRequest.url()));
        Response response;
        try {
            response = okHttp.newCall(okhttpRequest).execute();
        } catch (IOException e) {
            throw new CommunicationException(
                    String.format("Unable to execute '%s' to '%s'.", okhttpRequest.method(), okhttpRequest.url()),
                    e
            );
        }

        GravitonResponse gravitonResponse = new GravitonResponse(response, objectMapper);

        if(response.isSuccessful()) {
            LOG.info(String.format(
                    "Successful '%s' to '%s'. Response was '%d' - '%s'.",
                    okhttpRequest.method(),
                    okhttpRequest.url(),
                    response.code(),
                    response.message()
            ));
        } else {
            throw new CommunicationException(String.format(
                    "Failed '%s' to '%s'. Response was '%d' - '%s' with body '%s'.",
                    okhttpRequest.method(),
                    okhttpRequest.url(),
                    response.code(),
                    response.message(),
                    response.body()
            ));
        }
        return gravitonResponse;
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
