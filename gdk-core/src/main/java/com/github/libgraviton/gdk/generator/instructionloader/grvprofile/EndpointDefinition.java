package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Represents an endpoint definition of Graviton's main page. For example:
 *
 *  {
 *    $ref: "https://api.rel-pub.zgkb.evoja.ch/core/config/",
 *    profile: "https://api.rel-pub.zgkb.evoja.ch/schema/core/config/collection"
 *  }
 */
public class EndpointDefinition {

    @JsonProperty("$ref")
    @JsonPropertyDescription("")
    private String ref;

    @JsonProperty("profile")
    @JsonPropertyDescription("")
    private String profile;

    public String getRef() {
        return ref;
    }

    public String getProfile() {
        return profile;
    }

}
