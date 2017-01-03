package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EndpointManagerTest {

    private EndpointManager manager;

    @Before
    public void setup() {
        manager = new EndpointManager();
    }

    @Test
    public void testExistingEndpoint() throws Exception {
        String className = "aClassName";
        Endpoint endpoint = new Endpoint("endpoint://item", "endpoint://collection/");
        manager.addEndpoint(className, endpoint);
        assertTrue(manager.hasEndpoint(className));
        assertEquals(endpoint, manager.getEndpoint(className));
    }

    @Test(expected = NoCorrespondingEndpointException.class)
    public void testMissingEndpoint() throws Exception {
        String className = "aClassName";
        assertFalse(manager.hasEndpoint(className));
        manager.getEndpoint(className);
    }
}
