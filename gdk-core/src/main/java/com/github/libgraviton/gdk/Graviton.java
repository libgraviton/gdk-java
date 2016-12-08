package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.gateway.OkHttpGateway;
import com.github.libgraviton.gdk.data.GravitonBase;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import com.github.libgraviton.gdk.exception.SerializationException;
import com.github.libgraviton.gdk.serialization.JsonPatcher;
import okhttp3.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * This is the base class used for Graviton API calls.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class Graviton {

    private static final Logger LOG = LoggerFactory.getLogger(Graviton.class);

    public static final MediaType CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * Defines the base setUrl of the Graviton server
     */
    private String baseUrl;

    /**
     * The object mapper used to serialize / deserialize to / from JSON
     */
    private ObjectMapper objectMapper = getObjectMapper();

    private ObjectMapper getObjectMapper() {
        // TODO make dateformat and timezone configurable
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new ObjectMapper().setDateFormat(dateFormat);
    }

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

    /**
     * Returns the base url
     *
     * @return The base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Creates a new executable request builder to build and perform a new request.
     *
     * @return A new executable request builder
     */
    public GravitonRequest.ExecutableBuilder request() {
        return new GravitonRequest.ExecutableBuilder(this);
    }

    public GravitonRequest.ExecutableBuilder head(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).head();
    }

    public GravitonRequest.ExecutableBuilder head(GravitonBase resource) throws NoCorrespondingEndpointException {
        return head(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder head(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) head(endpointManager.getEndpoint(clazz.getName()).getUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder options(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).options();
    }

    public GravitonRequest.ExecutableBuilder options(GravitonBase resource) throws NoCorrespondingEndpointException {
        return options(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder options(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) options(endpointManager.getEndpoint(clazz.getName()).getUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder get(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).get();
    }

    public GravitonRequest.ExecutableBuilder get(GravitonBase resource) throws NoCorrespondingEndpointException {
        return get(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder get(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) get(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder delete(String url) {
        return (GravitonRequest.ExecutableBuilder) request().setUrl(url).delete();
    }

    public GravitonRequest.ExecutableBuilder delete(GravitonBase resource) throws NoCorrespondingEndpointException {
        return delete(extractId(resource), resource.getClass());
    }

    public GravitonRequest.ExecutableBuilder delete(String id, Class clazz) throws NoCorrespondingEndpointException {
        return (GravitonRequest.ExecutableBuilder) delete(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .addParam("id", id);
    }

    public GravitonRequest.ExecutableBuilder put(GravitonBase resource) throws NoCorrespondingEndpointException, SerializationException {
        return (GravitonRequest.ExecutableBuilder) request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getItemUrl())
                .addParam("id", extractId(resource))
                .put(serializeResource(resource));
    }

    public GravitonRequest.ExecutableBuilder patch(GravitonBase resource) throws NoCorrespondingEndpointException, SerializationException {
        JsonNode jsonNode = objectMapper.convertValue(resource, JsonNode.class);
        return (GravitonRequest.ExecutableBuilder) request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getItemUrl())
                .addParam("id", extractId(resource))
                .patch(serializeResource(JsonPatcher.getPatch(resource, jsonNode)));
    }

    public GravitonRequest.ExecutableBuilder post(GravitonBase resource) throws NoCorrespondingEndpointException, SerializationException {
        return (GravitonRequest.ExecutableBuilder) request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getUrl())
                .post(serializeResource(resource));
    }

    /**
     * Executes a given Graviton request.
     *
     * @param request The Graviton request
     *
     * @return The corresponding Graviton response
     *
     * @throws CommunicationException If the request was not successful
     */
    public GravitonResponse execute(GravitonRequest request) throws CommunicationException {
        LOG.info(String.format("Starting '%s' to '%s'...", request.getMethod(), request.getUrl()));
        LOG.debug("with request body '" + request.getBody() + "'");
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
                    request.getMethod(),
                    request.getUrl(),
                    response.getCode(),
                    response.getMessage(),
                    response.getBody()
            ));
        }
        return response;
    }

    /**
     * Extracts the id of a given Graviton resource.
     *
     * @param data The Graviton resource.
     *
     * @return The extracted id.
     */
    protected String extractId(GravitonBase data) {
        String id = data.getId();
        return id != null ? id : "";
    }

    protected void setGateway(OkHttpGateway gateway) {
        this.gateway = gateway;
    }

    private String serializeResource(Object data) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    String.format("Cannot serialize '%s' to json.", data.getClass().getName()),
                    e
            );
        }
    }
}
