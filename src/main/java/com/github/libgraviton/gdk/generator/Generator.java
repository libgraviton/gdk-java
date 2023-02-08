package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.workerbase.gdk.GravitonApi;
import com.github.libgraviton.workerbase.gdk.api.endpoint.EndpointManager;
import com.github.libgraviton.workerbase.gdk.api.endpoint.GeneratedEndpointManager;
import com.github.libgraviton.workerbase.gdk.api.endpoint.exception.UnableToPersistEndpointAssociationsException;
import com.github.libgraviton.workerbase.gdk.exception.CommunicationException;
import com.github.libgraviton.workerbase.gdk.generator.exception.GeneratorException;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * This is the POJO generator. It generates POJOs for all endpoints of a given GravitonApi instance.
 */
public class Generator {

    private final Logger LOG = LoggerFactory.getLogger(Generator.class);

    /**
     * The schema mapper which creates POJOs by given schemas
     */
    private SchemaMapper schemaMapper;

    /**
     * Configures the pojo2jsonschema generator
     */
    private GenerationConfig config;

    /**
     * The GravitonApi instance to generator POJOs for
     */
    private GravitonApi gravitonApi;

    /**
     * The generator instruction loader providing all endpoints
     */
    private GeneratorInstructionLoader instructionLoader;

    /**
     * Constructor
     *
     * @param config The generator config
     * @param gravitonApi The gravitonApi instance to generate POJOs for
     * @param instructionLoader The generator instruction loader which should be used
     *
     * @throws GeneratorException When the POJO generation fails
     */
    public Generator(
            GenerationConfig config,
            GravitonApi gravitonApi,
            GeneratorInstructionLoader instructionLoader
    ) throws GeneratorException {
        this(
                config,
                gravitonApi,
                instructionLoader,
                new SchemaMapper(instantiateRuleFactory(config), new SchemaGenerator())
        );
    }

    /**
     * Constructor
     *
     * @param config The generator config
     * @param gravitonApi The gravitonApi instance to generate POJOs for
     * @param instructionLoader The generator instruction loader which should be used
     * @param schemaMapper The schema mapper to use for generating the POJOs
     */
    public Generator(
            GenerationConfig config,
            GravitonApi gravitonApi,
            GeneratorInstructionLoader instructionLoader,
            SchemaMapper schemaMapper
    ) {
        this.config = config;
        this.gravitonApi = gravitonApi;
        this.instructionLoader = instructionLoader;
        this.schemaMapper = schemaMapper;
    }

    /**
     * Performs the POJO generation and persists a service to class mapping.
     *
     * @throws GeneratorException If the POJO generation failed
     */
    public void generate() throws GeneratorException, CommunicationException {
        List<GeneratorInstruction> generatorInstructions = instructionLoader.loadInstructions();
        EndpointManager endpointManager = gravitonApi.getEndpointManager();

        LOG.info("Generating POJO classes for Graviton '" + gravitonApi.getBaseUrl() + "'.");
        for (GeneratorInstruction definition : generatorInstructions) {
            String className = definition.getClassName();
            if (0 == className.length()) {
                LOG.info(
                        "Ignoring endpoint '{}' because it does not define any class.",
                        definition.getEndpoint().getItemUrl()
                );
                continue;
            }

            if(endpointManager.shouldIgnoreEndpoint(definition.getEndpoint())) {
                LOG.info(
                        "Ignoring endpoint '{}' because of white- / blacklist configuration.",
                        definition.getEndpoint().getItemUrl()
                );
                continue;
            }

            String packageName = generatePackageName(config.getTargetPackage(), definition.getPackageName());
            JCodeModel codeModel = new JCodeModel();
            try {
                schemaMapper.generate(codeModel, className, packageName, definition.getJsonSchema().toString());
                codeModel.build(config.getTargetDirectory());
            } catch (IOException e) {
               throw new GeneratorException("Unable to generate POJO.", e);
            }
            endpointManager.addEndpoint(
                    packageName + (packageName.length() > 0 ? '.' : "")  + definition.getClassName(),
                    definition.getEndpoint()
            );
        }
        try {
            if (endpointManager instanceof GeneratedEndpointManager) {
                ((GeneratedEndpointManager) endpointManager).persist();
            }
        } catch (UnableToPersistEndpointAssociationsException e) {
            throw new GeneratorException("Unable to persist endpoint -> POJO association.", e);
        }
    }

    /**
     * Generates a package name based on a given root and sub package name.
     *
     * @param rootPackage The root package name.
     * @param subPackage The sub package name.
     *
     * @return The generated package name.
     */
    private String generatePackageName(String rootPackage, String subPackage) {
        String packageName = "";
        if (null != rootPackage && rootPackage.length() > 1) {
            packageName += rootPackage;
        }
        if (null != subPackage && subPackage.length() > 0) {
            packageName += (packageName.length() > 0 ? "." : "") + subPackage;
        }
        return packageName;
    }

    /**
     * Instantiates a rule factory corresponding to a given generation config.
     *
     * @param config The generation config
     *
     * @return The rule factory
     *
     * @throws GeneratorException If the rule factory cannot be created.
     */
    private static RuleFactory instantiateRuleFactory(GenerationConfig config) throws GeneratorException {
        AnnotatorFactory annotatorFactory = new AnnotatorFactory();
        Annotator annotator = annotatorFactory.getAnnotator(
                annotatorFactory.getAnnotator(config.getAnnotationStyle()),
                annotatorFactory.getAnnotator(config.getCustomAnnotator())
        );
        RuleFactory ruleFactory;
        try {
            ruleFactory = config.getCustomRuleFactory().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GeneratorException("Unable to instantiate RuleFactory.", e);
        }
        ruleFactory.setAnnotator(annotator);
        ruleFactory.setGenerationConfig(config);
        return ruleFactory;
    }

}
