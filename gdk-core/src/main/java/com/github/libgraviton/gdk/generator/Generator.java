package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.ServiceManager;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.exception.GeneratorException;
import com.github.libgraviton.gdk.generator.exception.UnableToPersistServiceAssociationsException;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

/**
 * This is the POJO generator. It generates POJOs for all services of a given Graviton instance.
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
     * The generator instruction loader providing all services
     */
    private GeneratorInstructionLoader definitionProvider;

    /**
     * Constructor
     *
     * @param config The generator config
     * @param instructionLoader The generator instruction loader which should be used
     *
     * @throws GeneratorException When the POJO generation fails
     */
    public Generator(
            GenerationConfig config,
            Graviton graviton,
            GeneratorInstructionLoader instructionLoader
    ) throws GeneratorException {
        this.config = config;
        this.graviton = graviton;
        this.definitionProvider = instructionLoader;

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

        this.schemaMapper = new SchemaMapper(ruleFactory, new SchemaGenerator());
    }

    /**
     * Performs the POJO generation and persists a service -> class mapping.
     *
     * @throws GeneratorException If the POJO generation failed
     */
    public void generate() throws GeneratorException {
        List<GeneratorInstruction> generatorInstructions = definitionProvider.loadInstructions();
        ServiceManager serviceManager = graviton.getServiceManager();
        for (GeneratorInstruction definition : generatorInstructions) {
            String className = definition.getClassName();
            String packageName = definition.getPackageName();
            if (0 == className.length() || 0 == packageName.length()) {
                LOG.warn(
                        "Ignoring service '{}' because it does not define any package or class " +
                                "(package: '{}', class: '{}').",
                        definition.getService().getItemUrl(),
                        packageName,
                        className
                );
                continue;
            }
            JCodeModel codeModel = new JCodeModel();
            String targetPackage = config.getTargetPackage();
            packageName = (targetPackage.length() > 0 ? targetPackage + '.' : "" ) + packageName;
            try {
                schemaMapper.generate(codeModel, className, packageName, definition.getJsonSchema());
                codeModel.build(config.getTargetDirectory());
            } catch (IOException e) {
               throw new GeneratorException("Unable to generate POJO.", e);
            }
            serviceManager.addService(
                    packageName + '.' + definition.getClassName(),
                    definition.getService()
            );
        }
        try {
            if (serviceManager instanceof GeneratedServiceManager) {
                ((GeneratedServiceManager) serviceManager).persist();
            }
        } catch (UnableToPersistServiceAssociationsException e) {
            throw new GeneratorException("Unable to persist service -> POJO association.", e);
        }
    }


}
