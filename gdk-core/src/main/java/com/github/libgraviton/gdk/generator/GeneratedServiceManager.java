package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.Service;
import com.github.libgraviton.gdk.ServiceManager;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadServiceAssociationsException;
import com.github.libgraviton.gdk.generator.exception.UnableToPersistServiceAssociationsException;

import java.io.*;
import java.util.Map;

/**
 * Service manager for generated POJOs. This service manager is capable of serializing it's service -> POJO class
 * association to a file and deserialize it afterwards.
 */
public class GeneratedServiceManager extends ServiceManager {

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
     * @throws UnableToLoadServiceAssociationsException When the serialization file cannot be loaded.
     */
    public GeneratedServiceManager(
            File serializationFile,
            boolean loadExisting
    ) throws UnableToLoadServiceAssociationsException {
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
     * @throws UnableToLoadServiceAssociationsException When the serialization file cannot be loaded.
     */
    public GeneratedServiceManager(File serializationFile) throws UnableToLoadServiceAssociationsException {
        this(serializationFile, serializationFile.exists());
    }

    /**
     * Loads the service -> POJO class association from the serialization file.
     *
     * @return The number of currently loaded service -> POJO class associations.
     *
     * @throws UnableToLoadServiceAssociationsException When service loading is not possible / failed.
     */
    public int load() throws UnableToLoadServiceAssociationsException {
        if (!serializationFile.exists()) {
            throw new UnableToLoadServiceAssociationsException(
                    "Unable to load from file '" + serializationFile.getAbsolutePath() + "'. File does not exist."
            );
        }
        try {
            FileInputStream streamIn = new FileInputStream(serializationFile);
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            services = (Map<String, Service>) objectinputstream.readObject();
        } catch (IOException e) {
            throw new UnableToLoadServiceAssociationsException(
                    "Unable to access file '" + serializationFile.getAbsolutePath() + "'.",
                    e
            );
        } catch (ClassNotFoundException e) {
            throw new UnableToLoadServiceAssociationsException(
                    "Cannot deserialize from file '" + serializationFile.getAbsolutePath() +
                            "' because one ore multiple destination classes do not exist.",
                    e
            );
        }
        if (null == services) {
            throw new UnableToLoadServiceAssociationsException(
                    "Failed to load from file '" + serializationFile.getAbsolutePath() +
                            "'. File content may be incompatible."
            );
        }
        return services.size();
    }

    /**
     * Writes the service -> POJO class associations to the serialization file.
     *
     * @return The number of service -> POJO class associations written.
     */
    public int persist() throws UnableToPersistServiceAssociationsException {
        try {
            FileOutputStream fout = new FileOutputStream(serializationFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(services);
        } catch (IOException e) {
            throw new UnableToPersistServiceAssociationsException(
                    "Cannot persist to file '" + serializationFile.getAbsolutePath() + "'. An IO error occurred.",
                    e
            );
        }
        return services.size();
    }

}
