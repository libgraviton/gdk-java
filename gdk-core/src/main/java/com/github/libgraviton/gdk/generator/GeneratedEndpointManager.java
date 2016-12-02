package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.Endpoint;
import com.github.libgraviton.gdk.EndpointManager;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadEndpointAssociationsException;
import com.github.libgraviton.gdk.generator.exception.UnableToPersistEndpointAssociationsException;

import java.io.*;
import java.util.Map;

/**
 * Endpoint manager for generated POJOs. This service manager is capable of serializing it's service -> POJO class
 * association to a file and deserialize it afterwards.
 */
public class GeneratedEndpointManager extends EndpointManager {

    /**
     * The file holding the serialized service -> POJO class association.
     */
    private File serializationFile;

    /**
     * Constructor. Defines the serialization file.
     *
     * @param serializationFile The serialization file.
     * @param loadExisting Whether to load the serialization file or not.
     *
     * @throws UnableToLoadEndpointAssociationsException When the serialization file cannot be loaded.
     */
    public GeneratedEndpointManager(
            File serializationFile,
            boolean loadExisting
    ) throws UnableToLoadEndpointAssociationsException {
        this.serializationFile = serializationFile;
        if (loadExisting) {
            this.load();
        }
    }

    /**
     * Constructor. Defines the serialization file and tries to load it if it exists.
     *
     * @param serializationFile The serialization file.
     *
     * @throws UnableToLoadEndpointAssociationsException When the serialization file cannot be loaded.
     */
    public GeneratedEndpointManager(File serializationFile) throws UnableToLoadEndpointAssociationsException {
        this(serializationFile, serializationFile.exists());
    }

    /**
     * Loads the service endpoints -> POJO class association from the serialization file.
     *
     * @return The number of currently loaded service endpoints -> POJO class associations.
     *
     * @throws UnableToLoadEndpointAssociationsException When service endpoints loading is not possible / failed.
     */
    public int load() throws UnableToLoadEndpointAssociationsException {
        if (!serializationFile.exists()) {
            throw new UnableToLoadEndpointAssociationsException(
                    "Unable to load from file '" + serializationFile.getAbsolutePath() + "'. File does not exist."
            );
        }
        try {
            FileInputStream streamIn = new FileInputStream(serializationFile);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            endpoints = (Map<String, Endpoint>) objectinputstream.readObject();
        } catch (IOException e) {
            throw new UnableToLoadEndpointAssociationsException(
                    "Unable to deserialize file '" + serializationFile.getAbsolutePath() + "'.",
                    e
            );
        } catch (ClassNotFoundException e) {
            throw new UnableToLoadEndpointAssociationsException(
                    "Cannot deserialize from file '" + serializationFile.getAbsolutePath() +
                            "' because one ore multiple destination classes do not exist.",
                    e
            );
        } catch (ClassCastException e) {
            throw new UnableToLoadEndpointAssociationsException(
                    "Failed to load from file '" + serializationFile.getAbsolutePath() +
                            "'. File content is incompatible.",
                    e
            );
        }
        return endpoints.size();
    }

    /**
     * Writes the service -> POJO class associations to the serialization file.
     *
     * @return The number of service -> POJO class associations written.
     */
    public int persist() throws UnableToPersistEndpointAssociationsException {
        try {
            FileOutputStream fout = new FileOutputStream(serializationFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(endpoints);
        } catch (IOException e) {
            throw new UnableToPersistEndpointAssociationsException(
                    "Cannot persist to file '" + serializationFile.getAbsolutePath() + "'. An IO error occurred.",
                    e
            );
        }
        return endpoints.size();
    }

}