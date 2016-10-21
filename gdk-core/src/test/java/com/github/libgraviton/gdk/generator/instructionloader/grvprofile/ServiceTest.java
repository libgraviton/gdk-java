package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.net.URL;

public class ServiceTest {

    @Test
    public void testBeanJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        URL jsonUrl = getClass().getClassLoader().getResource("service/serviceTest.json");
        Service service = objectMapper.readValue(jsonUrl, Service.class);

        assertEquals(2, service.getEndpointDefinitions().size());
        assertEquals("service://some-service/", service.getEndpointDefinitions().get(0).getRef());
        assertEquals("service://some-service/profile", service.getEndpointDefinitions().get(0).getProfile());
        assertEquals("service://another-service", service.getEndpointDefinitions().get(1).getRef());
        assertEquals("service://another-service/profile", service.getEndpointDefinitions().get(1).getProfile());
    }

}
