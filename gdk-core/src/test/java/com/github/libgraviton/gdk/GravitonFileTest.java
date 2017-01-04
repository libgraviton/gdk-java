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

public class GravitonFileTest {

    private Graviton graviton;

    private GravitonFile gravitonFile;

    private String url = "http://someUrl";

    private String itemUrl = "http://someUrl/{id}";

    @Before
    public void setupService() throws Exception {
        EndpointManager endpointManager = mock(EndpointManager.class);
        Endpoint endpoint = mock(Endpoint.class);
        when(endpoint.getUrl()).thenReturn(url);
        when(endpoint.getItemUrl()).thenReturn(itemUrl);
        when(endpointManager.getEndpoint(anyString())).thenReturn(endpoint);

        graviton = mock(Graviton.class);
        when(graviton.request()).thenCallRealMethod();
        when(graviton.get(url)).thenCallRealMethod();
        when(graviton.getEndpointManager()).thenReturn(endpointManager);
        when(graviton.extractId(any(GravitonBase.class))).thenCallRealMethod();

        gravitonFile = new GravitonFile(graviton);
    }

    @Test
    public void testGetFile() throws Exception {
        GravitonRequest request = gravitonFile.getFile(url).build();
        assertEquals(0, request.getHeaders().get("Accept").all().size());
    }

    @Test
    public void testGetMetadata() throws Exception {
        GravitonRequest request = gravitonFile.getMetadata(url).build();
        assertEquals(1, request.getHeaders().get("Accept").all().size());
        verify(graviton, times(1)).get(url);
    }

    @Test
    public void testPost() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        String data = "some real data";

        GravitonRequest request = gravitonFile.post(data, resource).build();
        List<Part> parts = request.getParts();
        assertEquals(2, parts.size());

        Part part1 = parts.get(0);
        assertEquals("upload", part1.getFormName());
        assertEquals(data, part1.getBody());

        Part part2 = parts.get(1);
        assertNull(part2.getFormName());
        assertEquals(graviton.serializeResource(resource), part2.getBody());
    }

    @Test
    public void testPut() throws Exception {
        SimpleClass resource = new SimpleClass();
        resource.setId("111");
        String data = "some real data";

        GravitonRequest request = gravitonFile.put(data, resource).build();
        List<Part> parts = request.getParts();
        assertEquals(2, parts.size());

        Part part1 = parts.get(0);
        assertEquals("upload", part1.getFormName());
        assertEquals(data, part1.getBody());

        Part part2 = parts.get(1);
        assertNull(part2.getFormName());
        assertEquals(graviton.serializeResource(resource), part2.getBody());
    }

    @Test
    public void testPatch() throws Exception {
        SimpleClass resource = new SimpleClass();
        gravitonFile.patch(resource);
        verify(graviton, times(1)).patch(resource);
    }

    @Test
    public void testDelete() throws Exception {
        SimpleClass resource = new SimpleClass();
        gravitonFile.delete(resource);
        verify(graviton, times(1)).delete(resource);
    }
}
