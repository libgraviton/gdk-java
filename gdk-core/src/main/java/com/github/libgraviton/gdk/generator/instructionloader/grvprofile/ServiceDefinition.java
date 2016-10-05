package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Represents a service definition of Graviton's main page.
 */
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

}
