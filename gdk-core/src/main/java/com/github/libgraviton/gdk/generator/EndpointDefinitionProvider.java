package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.Graviton;

import java.util.List;

public interface EndpointDefinitionProvider {

    public List<EndpointDefinition> getEndpointDefinitions();

    public Graviton getGraviton();

}
