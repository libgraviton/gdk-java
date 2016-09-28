package com.github.libgraviton.gdk.generator.endpointdefinitionprovider.grvprofile;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("thirdparty")
public class ServiceList {

    @JsonProperty("services")
    private List<ServiceDefinition> services;

    public List<ServiceDefinition> getServices() {
        return services;
    }

}
