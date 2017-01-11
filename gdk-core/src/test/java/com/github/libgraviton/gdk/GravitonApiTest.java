package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.gateway.OkHttpGateway;
import com.github.libgraviton.gdk.data.NoopClass;
import com.github.libgraviton.gdk.data.SimpleClass;
import com.github.libgraviton.gdk.exception.CommunicationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GravitonApiTest {

    private GravitonApi gravitonApi;

    private GravitonResponse response;

    private String itemUrl = "http://someUrl/item123";

    private String endpointUrl = "http://someUrl/";

    private String baseUrl = "someBaseUrl";

    @Before
    public void setupService() throws Exception {
        EndpointManager endpointManager = mock(EndpointManager.class);
        Endpoint endpoint = mock(Endpoint.class);
        OkHttpGateway gateway = mock(OkHttpGateway.class);
        response = mock(GravitonResponse.class);
        when(response.isSuccessful()).thenReturn(true);
        when(endpoint.getItemUrl()).thenReturn(itemUrl);
        when(endpoint.getUrl()).thenReturn(endpointUrl);
        when(endpointManager.getEndpoint(anyString())).thenReturn(endpoint);
        when(gateway.execute(any(GravitonRequest.class))).thenReturn(response);

        gravitonApi = new GravitonApi(baseUrl, endpointManager);

        gravitonApi.setGateway(gateway);
    }

    @Test
    public void testGet() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = gravitonApi.get(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPut() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = gravitonApi.put(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPatch() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = gravitonApi.patch(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPost() throws Exception {
        SimpleClass resource = new SimpleClass();
        GravitonResponse actualResponse = gravitonApi.post(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testDelete() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = gravitonApi.delete(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testHead() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = gravitonApi.head(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testOptions() throws Exception {
        SimpleClass resource = new SimpleClass();
        GravitonResponse actualResponse = gravitonApi.options(resource).execute();
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

    @Test(expected = CommunicationException.class)
    public void testExecuteFail() throws Exception {
        when(response.isSuccessful()).thenReturn(false);
        when(response.getRequest()).thenReturn(mock(GravitonRequest.class));

        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        gravitonApi.get(resource).execute();
    }
}
