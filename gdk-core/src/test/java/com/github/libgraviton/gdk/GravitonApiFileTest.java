package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.multipart.Part;
import com.github.libgraviton.gdk.data.GravitonBase;
import com.github.libgraviton.gdk.data.SimpleClass;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class GravitonApiFileTest {

    private GravitonApi gravitonApi;

    private GravitonFileEndpoint gravitonFileEndpoint;

    private String url = "http://someUrl";

    private String itemUrl = "http://someUrl/{id}";

    @Before
    public void setupService() throws Exception {
        EndpointManager endpointManager = mock(EndpointManager.class);
        Endpoint endpoint = mock(Endpoint.class);
        when(endpoint.getUrl()).thenReturn(url);
        when(endpoint.getItemUrl()).thenReturn(itemUrl);
        when(endpointManager.getEndpoint(anyString())).thenReturn(endpoint);

        gravitonApi = mock(GravitonApi.class);
        when(gravitonApi.request()).thenCallRealMethod();
        when(gravitonApi.get(url)).thenCallRealMethod();
        when(gravitonApi.getEndpointManager()).thenReturn(endpointManager);
        when(gravitonApi.extractId(any(GravitonBase.class))).thenCallRealMethod();

        gravitonFileEndpoint = new GravitonFileEndpoint(gravitonApi);
    }

    @Test
    public void testGetFile() throws Exception {
        GravitonRequest request = gravitonFileEndpoint.getFile(url).build();
        assertEquals(0, request.getHeaders().get("Accept").all().size());
    }

    @Test
    public void testGetMetadata() throws Exception {
        GravitonRequest request = gravitonFileEndpoint.getMetadata(url).build();
        assertEquals(1, request.getHeaders().get("Accept").all().size());
        verify(gravitonApi, times(1)).get(url);
    }

    @Test
    public void testPost() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        String data = "some real data";

        GravitonRequest request = gravitonFileEndpoint.post(data, resource).build();
        List<Part> parts = request.getParts();
        assertEquals(2, parts.size());

        Part part1 = parts.get(0);
        assertEquals("upload", part1.getFormName());
        assertEquals(data, part1.getBody());

        Part part2 = parts.get(1);
        assertNull(part2.getFormName());
        assertEquals(gravitonApi.serializeResource(resource), part2.getBody());
    }

    @Test
    public void testPut() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        String data = "some real data";

        GravitonRequest request = gravitonFileEndpoint.put(data, resource).build();
        List<Part> parts = request.getParts();
        assertEquals(2, parts.size());

        Part part1 = parts.get(0);
        assertEquals("upload", part1.getFormName());
        assertEquals(data, part1.getBody());

        Part part2 = parts.get(1);
        assertNull(part2.getFormName());
        assertEquals(gravitonApi.serializeResource(resource), part2.getBody());
    }

    @Test
    public void testPatch() throws Exception {
        SimpleClass resource = new SimpleClass();
        gravitonFileEndpoint.patch(resource);
        verify(gravitonApi, times(1)).patch(resource);
    }

    @Test
    public void testDelete() throws Exception {
        SimpleClass resource = new SimpleClass();
        gravitonFileEndpoint.delete(resource);
        verify(gravitonApi, times(1)).delete(resource);
    }
}
