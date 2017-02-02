package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.api.Request;
import com.github.libgraviton.gdk.api.Response;
import com.github.libgraviton.gdk.api.endpoint.Endpoint;
import com.github.libgraviton.gdk.api.endpoint.EndpointManager;
import com.github.libgraviton.gdk.data.NoopClass;
import com.github.libgraviton.gdk.data.SimpleClass;
import com.github.libgraviton.gdk.exception.SerializationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GravitonApiTest {

    private GravitonApi gravitonApi;

    private Response response;

    private String itemUrl = "http://someUrl/item123";

    private String endpointUrl = "http://someUrl/";

    private String baseUrl = "someBaseUrl";

    @Before
    public void setupService() throws Exception {
        EndpointManager endpointManager = mock(EndpointManager.class);
        Endpoint endpoint = mock(Endpoint.class);
        RequestExecutor executor = mock(RequestExecutor.class);
        response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        when(endpoint.getItemUrl()).thenReturn(itemUrl);
        when(endpoint.getUrl()).thenReturn(endpointUrl);
        when(endpointManager.getEndpoint(anyString())).thenReturn(endpoint);
        when(executor.execute(any(Request.class))).thenReturn(response);

        gravitonApi = spy(new GravitonApi(baseUrl, endpointManager));
        gravitonApi.setRequestExecutor(executor);
    }

    @Test
    public void testGet() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        Response actualResponse = gravitonApi.get(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPut() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        Response actualResponse = gravitonApi.put(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPatch() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        Response actualResponse = gravitonApi.patch(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPost() throws Exception {
        SimpleClass resource = new SimpleClass();
        Response actualResponse = gravitonApi.post(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test(expected = SerializationException.class)
    public void testPostWithFailedSerialization() throws Exception {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(any(Object.class))).thenThrow(JsonProcessingException.class);
        doReturn(mapper).when(gravitonApi).getObjectMapper();

        SimpleClass resource = new SimpleClass();
        gravitonApi.post(resource).execute();
    }

    @Test
    public void testDelete() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        Response actualResponse = gravitonApi.delete(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testHead() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        Response actualResponse = gravitonApi.head(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testOptions() throws Exception {
        SimpleClass resource = new SimpleClass();
        Response actualResponse = gravitonApi.options(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testExtractId() throws Exception {
        NoopClass resourceWithoutId = new NoopClass();
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        assertEquals("", gravitonApi.extractId(resourceWithoutId));
        assertEquals("111", gravitonApi.extractId(resource));
    }
}
