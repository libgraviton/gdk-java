package com.github.libgraviton.gdk.maven;


import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.GeneratedServiceManager;
import com.github.libgraviton.gdk.generator.Generator;
import com.github.libgraviton.gdk.generator.instructionloader.grvprofile.GrvProfileInstructionLoader;
import com.github.libgraviton.gdk.generator.exception.GeneratorException;
import com.github.libgraviton.gdk.generator.exception.UnableToLoadEndpointAssociationsException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jsonschema2pojo.maven.Jsonschema2PojoMojo;

import java.io.File;
import java.io.IOException;

@Execute(goal = "generate", phase = LifecyclePhase.GENERATE_SOURCES)
@Mojo(name = "generate", threadSafe = true)
public class GenerateMojo extends Jsonschema2PojoMojo {

    @Parameter
    private String gravitonUrl;

    @Parameter
    private File pojoServiceAssocFile;

    @Parameter
    private Jsonschema2PojoMojo generatorConfig = new Jsonschema2PojoMojo();

    public void execute() throws MojoExecutionException
    {
        if (null == gravitonUrl) {
            throw new MojoExecutionException(
                    "Plugin configuration 'gravitonUrl' must be specified."
            );
        }
        if (null == pojoServiceAssocFile) {
            throw new MojoExecutionException(
                    "Plugin configuration 'pojoServiceAssocFile' must be specified."
            );
        }

        try {
            if(!generatorConfig.getTargetDirectory().mkdirs()) {
                getLog().info("Target directory '" + generatorConfig.getTargetDirectory() + "' already exists. Skipping POJO generation.");
                return;
            }

            pojoServiceAssocFile.createNewFile();
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
        } catch (GeneratorException | IOException e) {
            throw new MojoExecutionException("POJO generation failed.", e);
        } catch (UnableToLoadEndpointAssociationsException e) {
            throw new MojoExecutionException(
                    "Endpoint manager tried to load service associations. This should never happen at this point.",
                    e
            );
        }
        getLog().info("POJO generation done.");
    }

}
