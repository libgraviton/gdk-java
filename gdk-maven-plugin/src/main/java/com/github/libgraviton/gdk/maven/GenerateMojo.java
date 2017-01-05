package com.github.libgraviton.gdk.maven;


import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.GeneratedEndpointManager;
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

@Execute(goal = "generate")
@Mojo(name = "generate", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateMojo extends Jsonschema2PojoMojo {

    @Parameter(required = true)
    private String gravitonUrl;

    @Parameter
    private Jsonschema2PojoMojo generatorConfig = new Jsonschema2PojoMojo();

    private String assocFilePath = "target/generated-sources/gdk-java/assoc";

    public void execute() throws MojoExecutionException
    {
        try {
            if(!generatorConfig.getTargetDirectory().mkdirs()) {
                getLog().info("Target directory '" + generatorConfig.getTargetDirectory() + "' already exists. Skipping POJO generation.");
                return;
            }

            File pojoServiceAssocFile = new File(assocFilePath);
            pojoServiceAssocFile.getParentFile().mkdirs();
            pojoServiceAssocFile.createNewFile();
            Graviton graviton = new Graviton(
                    gravitonUrl,
                    new GeneratedEndpointManager(pojoServiceAssocFile, false)
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
