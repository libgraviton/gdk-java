package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.ServiceManager;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.exception.GeneratorException;
import com.github.libgraviton.gdk.generator.exception.UnableToPersistEndpointAssociationsException;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

/**
 * This is the POJO generator. It generates POJOs for all endpoints of a given Graviton instance.
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
     * The Graviton instance to generator POJOs for
     */
    private Graviton graviton;

    /**
     * The generator instruction loader providing all endpoints
     */
    private GeneratorInstructionLoader instructionLoader;

    /**
     * Constructor
     *
     * @param config The generator config
     * @param graviton The graviton instance to generate POJOs for
     * @param instructionLoader The generator instruction loader which should be used
     *
     * @throws GeneratorException When the POJO generation fails
     */
    public Generator(
            GenerationConfig config,
            Graviton graviton,
            GeneratorInstructionLoader instructionLoader
    ) throws GeneratorException {
        this(
                config,
                graviton,
                instructionLoader,
                new SchemaMapper(instantiateRuleFactory(config), new SchemaGenerator())
        );
    }

    /**
     * Constructor
     *
     * @param config The generator config
     * @param graviton The graviton instance to generate POJOs for
     * @param instructionLoader The generator instruction loader which should be used
     * @param schemaMapper The schema mapper to use for generating the POJOs
     */
    public Generator(
            GenerationConfig config,
            Graviton graviton,
            GeneratorInstructionLoader instructionLoader,
            SchemaMapper schemaMapper
    ) {
        this.config = config;
        this.graviton = graviton;
        this.instructionLoader = instructionLoader;
        this.schemaMapper = schemaMapper;
    }

    /**
     * Performs the POJO generation and persists a service -> class mapping.
     *
     * @throws GeneratorException If the POJO generation failed
     */
    public void generate() throws GeneratorException {
        List<GeneratorInstruction> generatorInstructions = instructionLoader.loadInstructions();
        ServiceManager serviceManager = graviton.getServiceManager();
        for (GeneratorInstruction definition : generatorInstructions) {
            String className = definition.getClassName();
            if (0 == className.length()) {
                LOG.warn(
                        "Ignoring endpoint '{}' because it does not define any class.",
                        definition.getEndpoint().getUrl()
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
            serviceManager.addEndpoint(
                    packageName + (packageName.length() > 0 ? '.' : "")  + definition.getClassName(),
                    definition.getEndpoint()
            );
        }
        try {
            if (serviceManager instanceof GeneratedServiceManager) {
                ((GeneratedServiceManager) serviceManager).persist();
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
