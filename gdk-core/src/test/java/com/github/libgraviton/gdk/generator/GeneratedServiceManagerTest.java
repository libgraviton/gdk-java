package com.github.libgraviton.gdk.generator;

import static org.junit.Assert.*;

import com.github.libgraviton.gdk.Service;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadServiceAssociationsException;
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

    @Test
    public void testLoadFromInexistentFile() throws Exception {
        thrown.expect(UnableToLoadServiceAssociationsException.class);
        thrown.expectMessage("not exist");

        File serializationFile = File.createTempFile("service-associations-deleted", ".tmp");
        // make sure the file does not exist
        assertTrue(serializationFile.delete());
        assertFalse(serializationFile.exists());

        GeneratedServiceManager generatedServiceManager = new GeneratedServiceManager(serializationFile, false);
        generatedServiceManager.load();
    }

    @Test
    public void testLoadFromIncompatibleFile() throws Exception {
        thrown.expect(UnableToLoadServiceAssociationsException.class);
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
