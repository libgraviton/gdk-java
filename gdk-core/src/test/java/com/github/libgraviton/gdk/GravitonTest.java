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

public class GravitonTest {

    private Graviton graviton;

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

        graviton = new Graviton(baseUrl, endpointManager);

        graviton.setGateway(gateway);
    }

    @Test
    public void testGet() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = graviton.get(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPut() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = graviton.put(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPatch() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = graviton.patch(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testPost() throws Exception {
        SimpleClass resource = new SimpleClass();
        GravitonResponse actualResponse = graviton.post(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testDelete() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = graviton.delete(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testHead() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        GravitonResponse actualResponse = graviton.head(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testOptions() throws Exception {
        SimpleClass resource = new SimpleClass();
        GravitonResponse actualResponse = graviton.options(resource).execute();
        assertEquals(response, actualResponse);
    }

    @Test
    public void testExtractId() throws Exception {
        NoopClass resourceWithoutId = new NoopClass();
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        assertEquals("", graviton.extractId(resourceWithoutId));
        assertEquals("111", graviton.extractId(resource));
    }

    @Test(expected = CommunicationException.class)
    public void testExecuteFail() throws Exception {
        when(response.isSuccessful()).thenReturn(false);

        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        graviton.get(resource).execute();
    }
}
