package com.github.libgraviton.gdk.maven;


import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.GeneratedServiceManager;
import com.github.libgraviton.gdk.generator.Generator;
import com.github.libgraviton.gdk.generator.instructionloader.grvprofile.GrvProfileInstructionLoader;
import com.github.libgraviton.gdk.generator.exception.GeneratorException;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadServiceAssociationsException;
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
    private File pojoServiceAssocFile;

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
        } else if (null == pojoServiceAssocFile) {
            throw new MojoExecutionException(
                    "Plugin configuration 'pojoServiceAssocFile' must be specified."
            );
        }
        getLog().info("Generating POJO classes for Graviton: " + gravitonUrl);
        try {
            Graviton graviton = new Graviton(
                    gravitonUrl,
                    new GeneratedServiceManager(pojoServiceAssocFile, false)
            );
            Generator generator = new Generator(
                    generatorConfig,
                    graviton,
                    new GrvProfileInstructionLoader(graviton)
            );
            generator.generate();
        } catch (GeneratorException e) {
            throw new MojoExecutionException("POJO generation failed.", e);
        } catch (UnableToLoadServiceAssociationsException e) {
            throw new MojoExecutionException(
                    "Service manager tried to load service associations. This should never happen at this point.",
                    e
            );
        }
        getLog().info("POJO generation done.");
    }

}
