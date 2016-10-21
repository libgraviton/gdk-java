package com.github.libgraviton.gdk.generator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.github.libgraviton.gdk.Endpoint;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.exception.GeneratorException;
import com.sun.codemodel.JCodeModel;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.json.JSONObject;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.SchemaMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class GeneratorTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private Graviton graviton;

    private GeneratedServiceManager serviceManager;

    private GeneratorInstructionLoader instructionLoader;

    @Before
    public void setup() {
        // Setup instruction loader mock
        List<GeneratorInstruction> instructions = Arrays.asList(
                new GeneratorInstruction(
                        "SomeClass",
                        "subpackage",
                        new JSONObject(
                                "{\"x-matcher-hint\":1,\"type\":\"object\",\"properties\":" +
                                        "{\"property\":{\"type\":\"string\"}}}"
                        ),
                        new Endpoint("endpoint://some-endpoint")
                ),
                new GeneratorInstruction(
                        "AnotherClass",
                        "",
                        new JSONObject("{\"x-matcher-hint\":2,\"type\":\"object\",\"properties\":{}}"),
                        new Endpoint("endpoint://another-endpoint")
                ),
                new GeneratorInstruction(
                        "",
                        "some.package",
                        new JSONObject("{\"schema\":1}"),
                        new Endpoint("endpoint://no-class")
                )
        );
        instructionLoader = mock(GeneratorInstructionLoader.class);
        when(instructionLoader.loadInstructions()).thenReturn(instructions);
        when(instructionLoader.loadInstructions(anyBoolean())).thenReturn(instructions);

        // Setup service manager mock
        serviceManager = mock(GeneratedServiceManager.class);

        // Setup graviton mock
        graviton = mock(Graviton.class);
        when(graviton.getServiceManager()).thenReturn(serviceManager);
    }

    @DataProvider
    public static Object[][] packageNames() {
        return new Object[][] {
                {
                        "the.target", // configured target package
                        "the.target.subpackage", // expected package of SomeClass
                        "the.target" // expected package of AnotherClass
                },
                {
                        "",
                        "subpackage",
                        ""
                }
        };
    }

    @Test
    @UseDataProvider("packageNames")
    public void testInstructionProcessing(
            final String configTargetPackage, String firstPackageName, String secondPackageName
    ) throws Exception {
        SchemaMapper schemaMapper = mock(SchemaMapper.class);
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public String getTargetPackage() {
                return configTargetPackage;
            }
        };

        Generator generator = new Generator(config, graviton, instructionLoader, schemaMapper);
        generator.generate();

        verify(schemaMapper, times(2)).generate(any(JCodeModel.class), anyString(), anyString(), anyString());
        verify(schemaMapper, times(1)).generate(
                any(JCodeModel.class),
                eq("SomeClass"),
                eq(firstPackageName),
                // JSON may be re-arranged, wo we check the x-matcher-hint field
                contains("\"x-matcher-hint\":1")
        );
        verify(schemaMapper, times(1)).generate(
                any(JCodeModel.class),
                eq("AnotherClass"),
                eq(secondPackageName),
                // JSON may be re-arranged, wo we check the x-matcher-hint field
                contains("\"x-matcher-hint\":2")
        );

        verify(serviceManager, times(2)).addEndpoint(anyString(), any(Endpoint.class));
        verify(serviceManager, times(1)).addEndpoint(
                eq(firstPackageName + (firstPackageName.length() > 0 ? '.' : "") + "SomeClass"),
                eq(new Endpoint("endpoint://some-endpoint"))
        );
        verify(serviceManager, times(1)).addEndpoint(
                eq(secondPackageName + (secondPackageName.length() > 0 ? '.' : "") + "AnotherClass"),
                eq(new Endpoint("endpoint://another-endpoint"))
        );

        verify(serviceManager, times(1)).persist();
    }

    @Test
    @UseDataProvider("packageNames")
    public void testClassGeneration(
            final String configTargetPackage, String firstPackageName, String secondPackageName
    ) throws Exception {
        final File targetDir = Files.createTempDirectory("test-generator").toFile();
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public String getTargetPackage() {
                return configTargetPackage;
            }

            @Override
            public File getTargetDirectory() {
                return targetDir;
            }
        };

        Generator generator = new Generator(config, graviton, instructionLoader);
        generator.generate();

        assertTrue(new File(new File(targetDir, firstPackageName.replace('.', '/')), "SomeClass.java").exists());
        assertTrue(new File(new File(targetDir, secondPackageName.replace('.', '/')), "AnotherClass.java").exists());
    }

    @Test
    public void testFailingGeneration() throws Exception {
        thrown.expect(GeneratorException.class);
        thrown.expectMessage("Unable to generate");

        SchemaMapper schemaMapper = mock(SchemaMapper.class);
        when(schemaMapper.generate(any(JCodeModel.class), anyString(), anyString(), anyString()))
                .thenThrow(new IOException());

        Generator generator = new Generator(new DefaultGenerationConfig(), graviton, instructionLoader, schemaMapper);
        generator.generate();
    }

}
