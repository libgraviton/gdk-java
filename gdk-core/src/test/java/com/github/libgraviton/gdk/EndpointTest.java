package com.github.libgraviton.gdk;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EndpointTest {

    private Endpoint endpoint;

    @Before
    public void setupService() {
        endpoint = new Endpoint("endpoint://item", "endpoint://collection/");
    }

    @Test
    public void testEqualsSameNotNull() {
        Endpoint equalEndpoint = new Endpoint("endpoint://item", "endpoint://collection/");
        assertTrue(endpoint.equals(equalEndpoint));
        assertTrue(equalEndpoint.equals(endpoint));
    }

    @Test
    public void testEqualsSameNull() {
        Endpoint endpoint = new Endpoint(null, null);
        Endpoint equalEndpoint = new Endpoint(null, null);
        assertTrue(endpoint.equals(equalEndpoint));
        assertTrue(equalEndpoint.equals(endpoint));
    }

    @Test
    public void testDifferentNotNull() {
        Endpoint equalEndpoint = new Endpoint("endpoint://other/item", "endpoint://other/collection/");
        assertFalse(endpoint.equals(equalEndpoint));
        assertFalse(equalEndpoint.equals(endpoint));
    }

    @Test
    public void testDifferentNull() {
        Endpoint equalEndpoint = new Endpoint(null, null);
        assertFalse(endpoint.equals(equalEndpoint));
        assertFalse(equalEndpoint.equals(endpoint));
    }

}

