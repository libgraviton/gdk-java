package com.github.libgraviton.gdk.api.endpoint;

import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all available endpoints of and associates them with a corresponding POJO class.
 */
public class EndpointManager {

    /**
     * The POJO class to endpoint association.
     */
    protected Map<String, Endpoint> endpoints = new HashMap<>();

    protected EndpointInclusionStrategy strategy;

    /**
     * Adds a endpoint and associates it with a given POJO class.
     *
     * @param className The POJO class name.
     * @param endpoint The endpoint.
     *
     * @return The number of currently added endpoints.
     */
    public int addEndpoint(String className, Endpoint endpoint) {
        endpoints.put(className, endpoint);
        return endpoints.size();
    }

    /**
     * Tells whether the service manager is aware of an endpoint for a given POJO class.
     *
     * @param className The POJO class name.
     *
     * @return true when the service manager is aware of a service of the given POJO class, otherwise false.
     */
    public boolean hasEndpoint(String className) {
        return endpoints.containsKey(className);
    }

    /**
     * Gets the endpoint associated to the given class.
     *
     * @param className The class name.
     *
     * @return The associated service.
     */
    public Endpoint getEndpoint(String className) {
        if (!hasEndpoint(className)) {
            throw new NoCorrespondingEndpointException(className);
        }
        return endpoints.get(className);
    }

    /**
     * Determines if the endpoint should be ignore.
     * @param endpoint endpoint to check
     * @return true if the endpoint should be ignored
     */
    public boolean shouldIgnoreEndpoint(Endpoint endpoint) {
        return strategy.shouldIgnoreEndpoint(endpoint);
    }

    public void setEndpointInclusionStrategy(EndpointInclusionStrategy strategy) {
        this.strategy = strategy;
    }
}