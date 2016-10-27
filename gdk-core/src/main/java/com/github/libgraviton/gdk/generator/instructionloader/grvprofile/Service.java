package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

/**
 * Represents the list of service definitions of Graviton's main page.
 * For example:
 *
 * {
 *   endpoints: [
 *     {
 *       {
 *         $ref: "https://api.rel-pub.zgkb.evoja.ch/core/app/",
 *         profile: "https://api.rel-pub.zgkb.evoja.ch/schema/core/app/collection"
 *       },
 *       {
 *         $ref: "https://api.rel-pub.zgkb.evoja.ch/core/config/",
 *         profile: "https://api.rel-pub.zgkb.evoja.ch/schema/core/config/collection"
 *       }
 *   ]
 *   thirdparty: {
 *     swissquant: [
 *       {
 *         $ref: "https://api.rel-pub.zgkb.evoja.ch/3rdparty/swissquant/client/{clientId}",
 *         profile: "https://api.rel-pub.zgkb.evoja.ch/schema/3rdparty/swissquant/client/{clientId}/item"
 *       },
 *       {
 *         $ref: "https://api.rel-pub.zgkb.evoja.ch/3rdparty/swissquant/clientExperience/{clientId}",
 *         profile: "https://api.rel-pub.zgkb.evoja.ch/schema/3rdparty/swissquant/clientExperience/{clientId}/item"
 *       }
 *     ]
 *   }
 * }
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("thirdparty")
public class Service {

    @JsonProperty("services")
    private List<EndpointDefinition> endpointDefinitions;

    public List<EndpointDefinition> getEndpointDefinitions() {
        return endpointDefinitions;
    }

}
