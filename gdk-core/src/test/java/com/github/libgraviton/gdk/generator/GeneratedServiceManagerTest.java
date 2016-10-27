package com.github.libgraviton.gdk.generator;

import static org.junit.Assert.*;

import com.github.libgraviton.gdk.Endpoint;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadEndpointAssociationsException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class GeneratedServiceManagerTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void testLoadAndPersist() throws Exception {
        File serializationFile = File.createTempFile("endpoint-associations-", ".tmp");
        GeneratedServiceManager generatedServiceManager = new GeneratedServiceManager(serializationFile, false);

        String className = "some.ClassName";
        Endpoint endpoint = new Endpoint("endpoint://item", "endpoint://item/collection/");

        assertFalse(generatedServiceManager.hasEndpoint(className));
        generatedServiceManager.addEndpoint(className, endpoint);
        assertTrue(generatedServiceManager.hasEndpoint(className));

        assertEquals(1, generatedServiceManager.persist());

        generatedServiceManager = new GeneratedServiceManager(serializationFile, false);
        assertFalse(generatedServiceManager.hasEndpoint(className));
        assertEquals(1, generatedServiceManager.load());
        assertTrue(generatedServiceManager.hasEndpoint(className));
        assertEquals(generatedServiceManager.getEndpoint(className), endpoint);
    }

    @Test
    public void testLoadFromInexistentFile() throws Exception {
        thrown.expect(UnableToLoadEndpointAssociationsException.class);
        thrown.expectMessage("not exist");

        File serializationFile = File.createTempFile("endpoint-associations-deleted", ".tmp");
        // make sure the file does not exist
        assertTrue(serializationFile.delete());
        assertFalse(serializationFile.exists());

        GeneratedServiceManager generatedServiceManager = new GeneratedServiceManager(serializationFile, false);
        generatedServiceManager.load();
    }

    @Test
    public void testLoadFromIncompatibleFile() throws Exception {
        thrown.expect(UnableToLoadEndpointAssociationsException.class);
        thrown.expectMessage("incompatible");

        File serializationFile = File.createTempFile("incompatible", ".serialized");
        String content = "this is no compatible serialization";
        FileOutputStream fout = new FileOutputStream(serializationFile);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(content);

        assertTrue(serializationFile.exists());

        GeneratedServiceManager generatedServiceManager = new GeneratedServiceManager(serializationFile, false);
        generatedServiceManager.load();
    }

}
