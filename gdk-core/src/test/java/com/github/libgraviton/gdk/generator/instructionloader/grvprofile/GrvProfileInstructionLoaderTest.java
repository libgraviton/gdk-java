package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class GrvProfileInstructionLoaderTest {

    private GrvProfileInstructionLoader instructionLoader;

    private String someSchema;

    private String anotherSchema;

    private String someMoreSchema;

    @Mock Graviton graviton;

    @Before
    public void setupInstructionLoader() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        String service = IOUtils.toString(classLoader.getResource(
                "service/grvProfileInstructionLoaderTest.json"
        ).openStream());
        someSchema = IOUtils.toString(classLoader.getResource(
                "serviceSchema/grvProfileInstructionLoaderTest.someSchema.json"
        ).openStream());
        anotherSchema = IOUtils.toString(classLoader.getResource(
                "serviceSchema/grvProfileInstructionLoaderTest.anotherSchema.json"
        ).openStream());
        someMoreSchema = IOUtils.toString(classLoader.getResource(
                "serviceSchema/grvProfileInstructionLoaderTest.someMoreSchema.json"
        ).openStream());

        graviton = mock(Graviton.class, withSettings());
        setInternalState(graviton, "objectMapper", new ObjectMapper());

        when(graviton.getBaseUrl()).thenReturn("service://graviton");
        when(graviton.get(anyString(), any(Class.class))).thenCallRealMethod();
        when(graviton.get("service://graviton")).thenReturn(service);
        when(graviton.get("service://some-service/profile")).thenReturn(someSchema);
        when(graviton.get("service://another-service/profile")).thenReturn(anotherSchema);
        when(graviton.get("service://some-more-service/profile")).thenReturn(someMoreSchema);

        instructionLoader = new GrvProfileInstructionLoader(graviton);
    }

    @DataProvider
    public static Object[][] loadInstructionsData() {
        return new Object[][] {
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.someSchema.json", // schema file
                    "service://some-service/", // collection url
                    "service://some-service/{id}", // item url
                    "SomeServiceDocument", // expected class name
                    "whatever.someservice", // expected package name
                    0 // index in instruction list
                },
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.anotherSchema.json",
                    null,
                    "service://another-service",
                    "AnotherServiceDocument",
                    "whatever.anotherservice",
                    1
                },
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.someMoreSchema.json",
                    "service://some-more-service/",
                    "service://some-more-service/{id}",
                    "",
                    "",
                    2
                },
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.anotherMoreSchema.json",
                    "service://some-more-service/",
                    "service://some-more-service/{id}",
                    "AnotherMoreServiceDocument",
                    "",
                    2
                }
        };
    }

    @Test
    @UseDataProvider("loadInstructionsData")
    public void testLoadInstructions(
            String schemaFile,
            String expectedCollectionUrl,
            String expectedItemUrl,
            String expectedClassName,
            String expectedPackageName,
            int instructionIndex
    ) throws Exception{
        String schema = IOUtils.toString(getClass().getClassLoader().getResource(schemaFile).openStream());
        when(graviton.get(not(eq("service://graviton")))).thenReturn(schema);

        List<GeneratorInstruction> instructions = instructionLoader.loadInstructions();
        assertEquals(3, instructions.size());

        GeneratorInstruction instruction = instructions.get(instructionIndex);
        assertEquals(expectedClassName, instruction.getClassName());
        assertEquals(expectedPackageName, instruction.getPackageName());

        JSONObject schemaObject = new JSONObject(schema);

        if (!"".equals(expectedCollectionUrl) && schemaObject.has("items")) {
            schemaObject = schemaObject.getJSONObject("items");
        }

        assertEquals(schemaObject.toString(), instruction.getJsonSchema().toString());
        assertEquals(expectedCollectionUrl, instruction.getEndpoint().getSchemaUrl());
        assertEquals(expectedItemUrl, instruction.getEndpoint().getUrl());
    }

}
