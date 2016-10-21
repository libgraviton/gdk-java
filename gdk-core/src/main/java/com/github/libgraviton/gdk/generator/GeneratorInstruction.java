package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.Endpoint;
import org.json.JSONObject;

/**
 * Defines a endpoint and the corresponding classes, which will be generated.
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
     * The jsonschema of the endpoint. Will be passed to the generator to create the class.
     */
    private JSONObject jsonSchema;

    /**
     * The endpoint for which classes will be generated.
     */
    private Endpoint endpoint;

    /**
     * Constructor. Initializes all the things.
     *
     * @param className The name of the generated class.
     * @param packageName The sub-package name of the generated class.
     * @param jsonSchema The jsonschema of the endpoint.
     * @param endpoint The endpoint itself
     */
    public GeneratorInstruction(String className, String packageName, JSONObject jsonSchema, Endpoint endpoint) {
        this.className = className;
        this.packageName = packageName;
        this.jsonSchema = jsonSchema;
        this.endpoint = endpoint;
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
     * Gets the jsonschema of the endpoint.
     *
     * @return The jsonschema of the endpoint.
     */
    public JSONObject getJsonSchema() {
        return jsonSchema;
    }

    /**
     * Gets the endpoint for which classes will be generated.
     *
     * @return The endpoint for which classes will be generated.
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }
}
