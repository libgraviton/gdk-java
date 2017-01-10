package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.gateway.OkHttpGateway;
import com.github.libgraviton.gdk.data.GravitonBase;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.SerializationException;
import com.github.libgraviton.gdk.generator.GeneratedEndpointManager;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadEndpointAssociationsException;
import com.github.libgraviton.gdk.serialization.JsonPatcher;
import com.github.libgraviton.gdk.util.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
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

    /**
     * Defines the base setUrl of the Graviton server
     */
    private String baseUrl;

    /**
     * The object mapper used to serialize / deserialize to / from JSON
     */
    private ObjectMapper objectMapper;

    /**
     * The endpoint manager which is used
     */
    private EndpointManager endpointManager;

    /**
     * The http client for making http calls.
     */
    private OkHttpGateway gateway;

    private Properties properties;

    public Graviton() {
        setup();
        this.baseUrl = properties.getProperty("graviton.base.url");

        try {
            this.endpointManager = new GeneratedEndpointManager();
        } catch (UnableToLoadEndpointAssociationsException e) {
            throw new IllegalStateException(e);
        }
    }

    public Graviton(String baseUrl, EndpointManager endpointManager) {
        setup();
        this.endpointManager = endpointManager;
        this.baseUrl = baseUrl;
    }

    protected void setup() {
        try {
            this.properties = PropertiesLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load properties files.", e);
        }
        this.gateway = new OkHttpGateway();
        this.objectMapper = getObjectMapper();
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
    public GravitonRequest.Builder request() {
        return new GravitonRequest.Builder(this);
    }

    public GravitonRequest.Builder head(String url) {
        return request().setUrl(url).head();
    }

    public GravitonRequest.Builder head(GravitonBase resource) {
        return head(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder head(String id, Class clazz) {
        return head(endpointManager.getEndpoint(clazz.getName()).getUrl())
                .addParam("id", id);
    }

    public GravitonRequest.Builder options(String url) {
        return request().setUrl(url).options();
    }

    public GravitonRequest.Builder options(GravitonBase resource) {
        return options(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder options(String id, Class clazz) {
        return options(endpointManager.getEndpoint(clazz.getName()).getUrl())
                .addParam("id", id);
    }

    public GravitonRequest.Builder get(String url) {
        return request().setUrl(url).get();
    }

    public GravitonRequest.Builder get(GravitonBase resource) {
        return get(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder get(String id, Class clazz) {
        return get(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .addParam("id", id);
    }

    public GravitonRequest.Builder delete(String url) {
        return request().setUrl(url).delete();
    }

    public GravitonRequest.Builder delete(GravitonBase resource) {
        return delete(extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder delete(String id, Class clazz) {
        return delete(endpointManager.getEndpoint(clazz.getName()).getItemUrl())
                .addParam("id", id);
    }

    public GravitonRequest.Builder put(GravitonBase resource) throws SerializationException {
        return request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getItemUrl())
                .addParam("id", extractId(resource))
                .put(serializeResource(resource));
    }

    public GravitonRequest.Builder patch(GravitonBase resource) throws SerializationException {
        JsonNode jsonNode = objectMapper.convertValue(resource, JsonNode.class);
        return request()
                .setUrl(endpointManager.getEndpoint(resource.getClass().getName()).getItemUrl())
                .addParam("id", extractId(resource))
                .patch(serializeResource(JsonPatcher.getPatch(resource, jsonNode)));
    }

    public GravitonRequest.Builder post(GravitonBase resource) throws SerializationException {
        return request()
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
        if (LOG.isDebugEnabled() && request.getBody() != null) {
            LOG.debug("with request body '" + request.getBody() + "'");
        }

        GravitonResponse response = gateway.execute(request);
        response.setObjectMapper(objectMapper);

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
                    "Failed '%s' to '%s'. Response was '%d' - '%s' with body '%s'.",
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

    protected String serializeResource(Object data) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(
                    String.format("Cannot serialize '%s' to json.", data.getClass().getName()),
                    e
            );
        }
    }

    protected ObjectMapper getObjectMapper() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(properties.getProperty("graviton.date.format"));
        dateFormat.setTimeZone(TimeZone.getTimeZone(properties.getProperty("graviton.timezone")));
        return new ObjectMapper().setDateFormat(dateFormat);
    }
}
