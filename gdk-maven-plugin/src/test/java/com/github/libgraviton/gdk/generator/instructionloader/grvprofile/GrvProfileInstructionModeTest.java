package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.GravitonApi;
import com.github.libgraviton.gdk.api.Request;
import com.github.libgraviton.gdk.api.Response;
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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class GrvProfileInstructionModeTest {

    private GrvProfileInstructionLoader instructionLoader;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private GravitonApi gravitonApi;


    @Before
    public void setup() throws Exception {
        String service = FileUtils.readFileToString(
                new File("src/test/resources/service/grvProfileInstructionLoaderTest.json"), Charset.defaultCharset());
        String someSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.someSchema.json"), Charset.defaultCharset());
        String anotherSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.anotherSchema.json"), Charset.defaultCharset());
        String someMoreSchema = FileUtils.readFileToString(
                new File("src/test/resources/serviceSchema/grvProfileInstructionLoaderTest.someMoreSchema.json"), Charset.defaultCharset());

        gravitonApi = mock(GravitonApi.class, withSettings());
        when(gravitonApi.getBaseUrl()).thenReturn("http://gravitonApi");

        Response response1 = mock(Response.class);
        when(response1.getBodyItem(Service.class)).thenReturn(objectMapper.readValue(service, Service.class));
        Request.Builder builder1 = mock(Request.Builder.class);
        when(builder1.execute()).thenReturn(response1);
        when(gravitonApi.get("http://gravitonApi")).thenReturn(builder1);

        Response response2 = mock(Response.class);
        when(response2.getBody()).thenReturn(someSchema);
        Request.Builder builder2 = mock(Request.Builder.class);
        when(builder2.execute()).thenReturn(response2);
        when(gravitonApi.get("http://some-service/profile")).thenReturn(builder2);

        Response response3 = mock(Response.class);
        when(response3.getBody()).thenReturn(anotherSchema);
        Request.Builder builder3 = mock(Request.Builder.class);
        when(builder2.execute()).thenReturn(response3);
        when(gravitonApi.get("http://another-service/profile")).thenReturn(builder3);

        Response response4 = mock(Response.class);
        when(response4.getBody()).thenReturn(someMoreSchema);
        Request.Builder builder4 = mock(Request.Builder.class);
        when(builder4.execute()).thenReturn(response4);
        when(gravitonApi.get("http://some-more-service/profile")).thenReturn(builder4);

        instructionLoader = new GrvProfileInstructionLoader(gravitonApi);
    }

    @DataProvider
    public static Object[][] loadInstructionsData() {
        return new Object[][] {
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.someSchema.json", // schema file
                    "http://some-service/", // collection setUrl
                    "http://some-service/{id}", // item setUrl
                    "SomeServiceDocument", // expected class name
                    "whatever.someservice", // expected package name
                    0 // index in instruction list
                },
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.anotherSchema.json",
                    null,
                    "http://another-service",
                    "AnotherServiceDocument",
                    "whatever.anotherservice",
                    1
                },
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.someMoreSchema.json",
                    "http://some-more-service/",
                    "http://some-more-service/{id}",
                    "",
                    "",
                    2
                },
                {
                    "serviceSchema/grvProfileInstructionLoaderTest.anotherMoreSchema.json",
                    "http://some-more-service/",
                    "http://some-more-service/{id}",
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
        String schema = FileUtils.readFileToString(new File("src/test/resources/" + schemaFile), Charset.defaultCharset());
        Response response = mock(Response.class);
        doReturn(schema).when(response).getBody();
        Request.Builder builder = mock(Request.Builder.class);
        when(builder.execute()).thenReturn(response);
        when(gravitonApi.get(not(eq("http://gravitonApi")))).thenReturn(builder);

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

        assertTrue(expectedCollectionUrl == null || instruction.getEndpoint().getUrl().contains(new URL(expectedCollectionUrl).getPath()));
        assertTrue(expectedItemUrl == null || instruction.getEndpoint().getItemUrl().contains(new URL(expectedItemUrl).getPath()));
    }

}
