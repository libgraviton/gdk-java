package com.github.libgraviton.gdk.maven;


import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.EndpointMapper;
import com.github.libgraviton.gdk.generator.Generator;
import com.github.libgraviton.gdk.generator.endpointdefinitionprovider.grvprofile.GrvProfileEndpointDefinitionProvider;
import org.apache.maven.plugin.MojoExecutionException;
import org.jsonschema2pojo.maven.Jsonschema2PojoMojo;

import java.io.File;

/**
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @threadSafe
 */
public class GenerateMojo extends Jsonschema2PojoMojo {

    /**
     * @parameter
     */
    private String gravitonUrl;

    /**
     * @parameter
     */
    private File endpointSerializationFile;

    /**
     * @parameter
     */
    private Jsonschema2PojoMojo generatorConfig = new Jsonschema2PojoMojo();

    public void execute() throws MojoExecutionException
    {
        if (null == gravitonUrl) {
            throw new MojoExecutionException(
                    "Plugin configuration 'gravitonUrl' must be specified."
            );
        } else if (null == endpointSerializationFile) {
            throw new MojoExecutionException(
                    "Plugin configuration 'endpointSerializationFile' must be specified."
            );
        }
        getLog().info("Generating POJO classes for Graviton: " + gravitonUrl);
        Graviton graviton = new Graviton(gravitonUrl, new EndpointMapper(endpointSerializationFile));
        // @todo: make generation strategy configurable as soon as we provide multiple strategies
        Generator generator = new Generator(
                generatorConfig,
                new GrvProfileEndpointDefinitionProvider(graviton)
        );
        generator.generate();
        getLog().info("POJO generation done.");
    }

}
