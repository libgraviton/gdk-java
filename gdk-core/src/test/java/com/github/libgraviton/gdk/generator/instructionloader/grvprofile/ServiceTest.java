package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ServiceTest {

    @Test
    public void testBeanJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String serviceJson = FileUtils.readFileToString(
                new File("src/test/resources/service/serviceTest.json"));;
        Service service = objectMapper.readValue(serviceJson, Service.class);

        assertEquals(2, service.getEndpointDefinitions().size());
        assertEquals("service://some-service/", service.getEndpointDefinitions().get(0).getRef());
        assertEquals("service://some-service/profile", service.getEndpointDefinitions().get(0).getProfile());
        assertEquals("service://another-service", service.getEndpointDefinitions().get(1).getRef());
        assertEquals("service://another-service/profile", service.getEndpointDefinitions().get(1).getProfile());
    }

}
