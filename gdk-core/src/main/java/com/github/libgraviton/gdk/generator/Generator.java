package com.github.libgraviton.gdk.generator;

import com.github.libgraviton.gdk.generator.exception.GeneratorException;
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

    private EndpointDefinitionProvider definitionProvider;

    /**
     * Constructor
     *
     * @param config The generator config
     * @param definitionProvider The endpoint definition provider which should be used
     *
     * @throws GeneratorException When the POJO generation fails
     */
    public Generator(GenerationConfig config, EndpointDefinitionProvider definitionProvider) throws GeneratorException {
        this.config = config;
        this.definitionProvider = definitionProvider;
        RuleFactory ruleFactory;
        AnnotatorFactory annotatorFactory = new AnnotatorFactory();
        Annotator annotator = annotatorFactory.getAnnotator(
                annotatorFactory.getAnnotator(config.getAnnotationStyle()),
                annotatorFactory.getAnnotator(config.getCustomAnnotator())
        );
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
     * Performs the POJO generation and persists an endpoint -> class mapping.
     */
    public void generate() {
        List<EndpointDefinition> endpointDefinitions = definitionProvider.getEndpointDefinitions();
        EndpointMapper endpointMapper = definitionProvider.getGraviton().getEndpointMapper();
        for (EndpointDefinition definition : endpointDefinitions) {
            String className = definition.getClassName();
            String packageName = definition.getPackageName();
            if (0 == className.length() || 0 == packageName.length()) {
                LOG.warn(
                        "Ignoring endpoint '{}' because it does not define any package or class " +
                                "(package: '{}', class: '{}').",
                        definition.getEndpointUrl(),
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
            endpointMapper.map(packageName + '.' + definition.getClassName(), definition.getEndpointUrl());
        }
        endpointMapper.persist();
    }


}
