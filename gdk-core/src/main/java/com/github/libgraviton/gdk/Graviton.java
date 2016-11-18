package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.gateway.OkHttpGateway;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import com.github.libgraviton.gdk.exception.SerializationException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * Defines the base setUrl of the Graviton server
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
    private OkHttpGateway gateway;

    /**
     * Constructor
     *
     * @param baseUrl The base setUrl pointing to the Graviton server
     * @param endpointManager The endpoint manager to use
     */
    public Graviton(String baseUrl, EndpointManager endpointManager) {
        this.baseUrl = baseUrl;
        this.endpointManager = endpointManager;
        this.gateway = new OkHttpGateway();
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

    public GravitonRequest.Builder request() {
        return new GravitonRequest.ExecutableBuilder(this);
    }

    public GravitonRequest.ExecutableBuilder head(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).head();
    }

    public GravitonRequest.ExecutableBuilder head(Object resource) throws NoCorrespondingEndpointException {
        return head(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder head(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) head(endpointManager.getEndpoint(clazz.getName()).getUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder get(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).get();
    }

    public GravitonRequest.ExecutableBuilder get(Object resource) throws NoCorrespondingEndpointException {
        return get(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder get(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) get(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder delete(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).delete();
    }

    public GravitonRequest.ExecutableBuilder delete(Object resource) throws NoCorrespondingEndpointException {
        return delete(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder delete(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) delete(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder put(Object resource) throws NoCorrespondingEndpointException, SerializationException {
        return (GravitonRequest.ExecutableBuilder) request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getItemUrl())
                .addParam("id", extractId(resource))
                .put(serializeResource(resource));
    }

    public GravitonRequest.ExecutableBuilder post(Object resource) throws NoCorrespondingEndpointException, SerializationException {
        return (GravitonRequest.ExecutableBuilder) request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getUrl())
                .post(serializeResource(resource));
    }

    public GravitonResponse execute(GravitonRequest request) throws CommunicationException {
        LOG.info(String.format("Starting '%s' to '%s'...", request.getMethod(), request.getUrl()));
        GravitonResponse response = gateway.execute(request);

        if(response.isSuccessful()) {
            LOG.info(String.format(
                    "Successful '%s' to '%s'. Response was '%d' - '%s'.",
                    request.getMethod(),
                    request.getUrl(),
                    response.getCode(),
                    response.getMessage()
            ));
        } else {
            throw new CommunicationException(String.format(
                    "Failed '%s' to '%s'. Response was '%d' - '%s' with setBody '%s'.",
                    request.getUrl(),
                    request.getUrl(),
                    response.getCode(),
                    response.getMessage(),
                    response.getBody()
            ));
        }
        return response;
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

    protected void setGateway(OkHttpGateway gateway) {
        this.gateway = gateway;
    }

    private String serializeResource(Object data) throws SerializationException {
        String json;
        try {
            json = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    String.format("Cannot serialize '%s' to json.", data.getClass().getName()),
                    e
            );
        }
        return json;
    }
}
