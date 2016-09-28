package com.github.libgraviton.gdk.generator.endpointdefinitionprovider.grvprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ServiceDefinition {

    @JsonProperty("$ref")
    @JsonPropertyDescription("")
    private String $ref;

    @JsonProperty("profile")
    @JsonPropertyDescription("")
    private String profile;

    public String get$ref() {
        return $ref;
    }

    public String getProfile() {
        return profile;
    }

    public boolean isCollection() {
        return true;
    }
}
