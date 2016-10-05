package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.net.URL;

public class ServiceListTest {

    @Test
    public void testBeanJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        URL jsonUrl = getClass().getClassLoader().getResource("json/grvServiceList.json");
        ServiceList serviceList = objectMapper.readValue(jsonUrl, ServiceList.class);

        assertEquals(2, serviceList.getServices().size());
        assertEquals("service://some-service/", serviceList.getServices().get(0).get$ref());
        assertEquals("service://some-service/profile", serviceList.getServices().get(0).getProfile());
        assertEquals("service://another-service/", serviceList.getServices().get(1).get$ref());
        assertEquals("service://another-service/profile", serviceList.getServices().get(1).getProfile());
    }

}
