package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

/**
 * Represents the list of service definitions of Graviton's main page.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("thirdparty")
public class ServiceList {

    @JsonProperty("services")
    private List<ServiceDefinition> services;

    public List<ServiceDefinition> getServices() {
        return services;
    }

}
