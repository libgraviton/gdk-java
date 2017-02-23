package com.github.libgraviton.gdk.maven;


import com.github.libgraviton.gdk.GravitonApi;
import com.github.libgraviton.gdk.api.endpoint.EndpointInclusionStrategy;
import com.github.libgraviton.gdk.api.endpoint.GeneratedEndpointManager;
import com.github.libgraviton.gdk.api.endpoint.exception.UnableToLoadEndpointAssociationsException;
import com.github.libgraviton.gdk.generator.Generator;
import com.github.libgraviton.gdk.generator.GeneratorInstructionLoader;
import com.github.libgraviton.gdk.generator.instructionloader.grvprofile.GrvProfileInstructionLoader;
import com.github.libgraviton.gdk.generator.instructionloader.swagger.SwaggerInstructionLoader;
import com.github.libgraviton.gdk.generator.exception.GeneratorException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jsonschema2pojo.maven.Jsonschema2PojoMojo;

@Execute(goal = "generate")
@Mojo(name = "generate", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateMojo extends Jsonschema2PojoMojo {

    @Parameter(required = true)
    private String gravitonUrl;

    @Parameter(required = false)
    private String endpointBlacklistPath;

    @Parameter(required = false)
    private String endpointWhitelistPath;

    @Parameter
    private Jsonschema2PojoMojo generatorConfig = new Jsonschema2PojoMojo();

    @Parameter(defaultValue = GrvProfileInstructionLoader.ID)
    private String instructionLoader;

    @Parameter
    private SwaggerConfig swaggerConfig = new SwaggerConfig();

    public void execute() throws MojoExecutionException
    {
        try {
            if(!generatorConfig.getTargetDirectory().mkdirs()) {
                getLog().info("Target directory '" + generatorConfig.getTargetDirectory() + "' already exists. Skipping POJO generation.");
                return;
            }

            GeneratedEndpointManager endpointManager = new GeneratedEndpointManager(GeneratedEndpointManager.Mode.CREATE);
            endpointManager.setEndpointInclusionStrategy(getEndpointInclusionStrategy());
            GravitonApi gravitonApi = new GravitonApi(
                    gravitonUrl,
                    endpointManager
            );
            Generator generator = new Generator(
                    generatorConfig,
                    gravitonApi,
                    createInstructionLoader(gravitonApi)
            );
            generator.generate();
        } catch (GeneratorException e) {
            throw new MojoExecutionException("POJO generation failed.", e);
        } catch (UnableToLoadEndpointAssociationsException e) {
            throw new MojoExecutionException(
                    "Endpoint manager tried to load service associations. This should never happen at this point.",
                    e
            );
        }
        getLog().info("POJO generation done.");
    }

    protected EndpointInclusionStrategy getEndpointInclusionStrategy() throws MojoExecutionException {
        if (endpointBlacklistPath != null && endpointWhitelistPath != null) {
            throw new MojoExecutionException("Configuring a 'blacklistPath' and a 'whitelistPath' is not allowed.");
        }

        EndpointInclusionStrategy endpointInclusionStrategy;
        if (endpointBlacklistPath != null) {
            endpointInclusionStrategy = EndpointInclusionStrategy
                    .create(EndpointInclusionStrategy.Strategy.BLACKLIST, endpointBlacklistPath);
        } else if (endpointWhitelistPath != null) {
            endpointInclusionStrategy = EndpointInclusionStrategy
                    .create(EndpointInclusionStrategy.Strategy.WHITELIST, endpointWhitelistPath);
        } else {
            endpointInclusionStrategy = EndpointInclusionStrategy
                    .create(EndpointInclusionStrategy.Strategy.DEFAULT, null);
        }
        return endpointInclusionStrategy;
    }

    private GeneratorInstructionLoader createInstructionLoader(GravitonApi graviton) throws MojoExecutionException {
        switch (instructionLoader) {
            case GrvProfileInstructionLoader.ID:
                return new GrvProfileInstructionLoader(graviton);
            case SwaggerInstructionLoader.ID:
                return new SwaggerInstructionLoader(swaggerConfig.getLocation());
        }
        throw new MojoExecutionException(String.format(
                "Unsupported generator instruction loader: '%s'. Supported loaders: '%s', '%s'.",
                instructionLoader,
                GrvProfileInstructionLoader.ID,
                SwaggerInstructionLoader.ID
        ));
    }

}
