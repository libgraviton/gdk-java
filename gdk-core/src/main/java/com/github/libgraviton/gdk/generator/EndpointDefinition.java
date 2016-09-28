package com.github.libgraviton.gdk.generator;

/**
 * Created by tgdpaad2 on 22/09/16.
 */
public class EndpointDefinition {

    private String className;

    private String packageName;

    private String jsonSchema;

    private String endpointUrl;

    public EndpointDefinition(String className, String packageName, String jsonSchema, String endpointUrl) {
        this.className = className;
        this.packageName = packageName;
        this.jsonSchema = jsonSchema;
        this.endpointUrl = endpointUrl;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }
}
