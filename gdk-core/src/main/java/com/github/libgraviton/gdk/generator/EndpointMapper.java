package com.github.libgraviton.gdk.generator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tgdpaad2 on 28/09/16.
 */
public class EndpointMapper {

    private File serializationFile;

    private Map<String, String> endpointMapping;

    public EndpointMapper(File serializationFile) {
        this.serializationFile = serializationFile;
    }

    public int load() {
        if (serializationFile.exists()) {
            try {
                FileInputStream streamIn = new FileInputStream(serializationFile);
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
                endpointMapping = (Map<String, String>) objectinputstream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                // todo
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int persist() {
        try {
            FileOutputStream fout = new FileOutputStream(serializationFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(endpointMapping);
            return endpointMapping.size();
        } catch (IOException e) {
            // todo
            e.printStackTrace();
        }
        return 0;
    }

    public String getEndpoint(String className) throws Exception {
        if (null == endpointMapping) {
            load();
        }
        if (endpointMapping.containsKey(className)) {
            return endpointMapping.get(className);
        }
        // @todo
        throw new Exception("No endpoint defined for class.");
    }

    public void map(String className, String endpoint) {
        if (null == endpointMapping) {
            endpointMapping = new HashMap<>();
        }
        endpointMapping.put(className, endpoint);
    }

}
