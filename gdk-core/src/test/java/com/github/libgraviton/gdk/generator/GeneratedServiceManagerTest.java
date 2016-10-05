package com.github.libgraviton.gdk.generator;

import static org.junit.Assert.*;

import com.github.libgraviton.gdk.Service;
import org.junit.Test;

import java.io.File;


public class GeneratedServiceManagerTest {

    @Test
    public void testLoadAndPersist() throws Exception {
        File serializationFile = File.createTempFile("service-associations-", ".tmp");
        GeneratedServiceManager generatedServiceManager = new GeneratedServiceManager(serializationFile, false);

        String className = "some.ClassName";
        Service service = new Service("service://item", "service://item/collection/");

        assertFalse(generatedServiceManager.hasService(className));
        generatedServiceManager.addService(className, service);
        assertTrue(generatedServiceManager.hasService(className));

        assertEquals(1, generatedServiceManager.persist());

        generatedServiceManager = new GeneratedServiceManager(serializationFile, false);
        assertFalse(generatedServiceManager.hasService(className));
        assertEquals(1, generatedServiceManager.load());
        assertTrue(generatedServiceManager.hasService(className));
        assertEquals(generatedServiceManager.getService(className), service);
    }

}
