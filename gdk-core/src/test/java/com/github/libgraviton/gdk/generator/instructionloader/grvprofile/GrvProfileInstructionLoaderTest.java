package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(DataProviderRunner.class)
public class GrvProfileInstructionLoaderTest {

    private GrvProfileInstructionLoader instructionLoader;

    @Mock Graviton graviton;

    @Before
    public void setupInstructionLoader() throws Exception {
        String service = FileUtils.readFileToString(
                new File("src/test/resources/service/grvProfileInstructionLoaderTest.json"));
        String someSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.someSchema.json"));
        String anotherSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.anotherSchema.json"));
        String someMoreSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.someMoreSchema.json"));

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
        String schema = FileUtils.readFileToString(
                new File("src/test/resources/" + schemaFile));
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
