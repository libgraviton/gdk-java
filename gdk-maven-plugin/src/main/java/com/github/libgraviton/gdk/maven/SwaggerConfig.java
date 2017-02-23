package com.github.libgraviton.gdk.maven;

import org.apache.maven.plugins.annotations.Parameter;

public class SwaggerConfig {

    @Parameter(required = true)
    private String location;

    public String getLocation() {
        return location;
    }
}
