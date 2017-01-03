package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
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

@RunWith(DataProviderRunner.class)
public class GrvProfileInstructionLoaderTest {

    private GrvProfileInstructionLoader instructionLoader;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    Graviton graviton;


    @Before
    public void setup() throws Exception {
        String service = FileUtils.readFileToString(
                new File("src/test/resources/service/grvProfileInstructionLoaderTest.json"));
        String someSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.someSchema.json"));
        String anotherSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.anotherSchema.json"));
        String someMoreSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.someMoreSchema.json"));

        graviton = mock(Graviton.class, withSettings());
        when(graviton.getBaseUrl()).thenReturn("service://graviton");

        GravitonResponse response1 = mock(GravitonResponse.class);
        when(response1.getBody(Service.class)).thenReturn(objectMapper.readValue(service, Service.class));
        GravitonRequest.ExecutableBuilder builder1 = mock(GravitonRequest.ExecutableBuilder.class);
        when(builder1.execute()).thenReturn(response1);
        when(graviton.get("service://graviton")).thenReturn(builder1);

        GravitonResponse response2 = mock(GravitonResponse.class);
        when(response2.getBody()).thenReturn(someSchema);
        GravitonRequest.ExecutableBuilder builder2 = mock(GravitonRequest.ExecutableBuilder.class);
        when(builder2.execute()).thenReturn(response2);
        when(graviton.get("service://some-service/profile")).thenReturn(builder2);

        GravitonResponse response3 = mock(GravitonResponse.class);
        when(response3.getBody()).thenReturn(anotherSchema);
        GravitonRequest.ExecutableBuilder builder3 = mock(GravitonRequest.ExecutableBuilder.class);
        when(builder2.execute()).thenReturn(response3);
        when(graviton.get("service://another-service/profile")).thenReturn(builder3);

        GravitonResponse response4 = mock(GravitonResponse.class);
        when(response4.getBody()).thenReturn(someMoreSchema);
        GravitonRequest.ExecutableBuilder builder4 = mock(GravitonRequest.ExecutableBuilder.class);
        when(builder4.execute()).thenReturn(response4);
        when(graviton.get("service://some-more-service/profile")).thenReturn(builder4);

        instructionLoader = new GrvProfileInstructionLoader(graviton);
    }

    @DataProvider
    public static Object[][] loadInstructionsData() {
        return new Object[][] {
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.someSchema.json", // schema file
                    "service://some-service/", // collection setUrl
                    "service://some-service/{id}", // item setUrl
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
        String schema = FileUtils.readFileToString(new File("src/test/resources/" + schemaFile));
        GravitonResponse response = mock(GravitonResponse.class);
        doReturn(schema).when(response).getBody();
        GravitonRequest.ExecutableBuilder builder = mock(GravitonRequest.ExecutableBuilder.class);
        when(builder.execute()).thenReturn(response);
        when(graviton.get(not(eq("service://graviton")))).thenReturn(builder);

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
        assertEquals(expectedCollectionUrl, instruction.getEndpoint().getUrl());
        assertEquals(expectedItemUrl, instruction.getEndpoint().getItemUrl());
    }

}
