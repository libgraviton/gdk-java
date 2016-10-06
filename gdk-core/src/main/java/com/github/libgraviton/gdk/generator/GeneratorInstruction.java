package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.Service;
import org.json.JSONObject;

/**
 * Defines a service and the corresponding classes, which will be generated.
 */
public class GeneratorInstruction {

    /**
     * The name of the generated class.
     */
    private String className;

    /**
     * The sub-package name of the generated class.
     */
    private String packageName;

    /**
     * The jsonschema of the service. Will be passed to the generator to create the class.
     */
    private JSONObject jsonSchema;

    /**
     * The service for which classes will be generated.
     */
    private Service service;

    /**
     * Constructor. Initializes all the things.
     *
     * @param className The name of the generated class.
     * @param packageName The sub-package name of the generated class.
     * @param jsonSchema The jsonschema of the service.
     * @param service The service itself
     */
    public GeneratorInstruction(String className, String packageName, JSONObject jsonSchema, Service service) {
        this.className = className;
        this.packageName = packageName;
        this.jsonSchema = jsonSchema;
        this.service = service;
    }

    /**
     * Gets the name of the generated class.
     *
     * @return The name of the generated class.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the sub-package name of the generated class.
     *
     * @return The sub-package name of the generated class.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the jsonschema of the service.
     *
     * @return The jsonschema of the service.
     */
    public JSONObject getJsonSchema() {
        return jsonSchema;
    }

    /**
     * Gets the service for which classes will be generated.
     *
     * @return The service for which classes will be generated.
     */
    public Service getService() {
        return service;
    }
}
