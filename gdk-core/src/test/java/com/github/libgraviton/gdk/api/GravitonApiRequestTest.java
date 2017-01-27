package com.github.libgraviton.gdk.api;

import com.github.libgraviton.gdk.GravitonApi;
import com.github.libgraviton.gdk.api.multipart.Part;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class GravitonApiRequestTest {

    private GravitonRequest.Builder builder;

    @Before
    public void setup() throws Exception {
        GravitonApi gravitonApi = mock(GravitonApi.class);
        builder = new GravitonRequest.Builder(gravitonApi).setUrl("http://aRandomUrl");
    }

    @Test
    public void testBuilderGetUrl() throws Exception {
        builder.setUrl("http://someUrl/{id}").addParam("id","someId");
        assertEquals("http://someUrl/someId", builder.buildUrl().toString());
    }

    @Test
    public void testGet() throws Exception {
        GravitonRequest request = builder.get().build();
        assertEquals(HttpMethod.GET, request.getMethod());
        assertNull(request.getBody());
    }

    @Test
    public void testPut() throws Exception {
        String data = "putData";
        GravitonRequest request = builder.put(data).build();
        assertEquals(HttpMethod.PUT, request.getMethod());
        assertEquals(data, request.getBody());
    }

    @Test
    public void testPost() throws Exception {
        String data = "postData";
        GravitonRequest request = builder.post(data).build();
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals(data, request.getBody());
    }

    @Test
    public void testPatch() throws Exception {
        String data = "patchData";
        GravitonRequest request = builder.patch(data).build();
        assertEquals(HttpMethod.PATCH, request.getMethod());
        assertEquals(data, request.getBody());
    }

    @Test
    public void testDelete() throws Exception {
        GravitonRequest request = builder.delete().build();
        assertEquals(HttpMethod.DELETE, request.getMethod());
        assertNull(request.getBody());
    }

    @Test
    public void testHead() throws Exception {
        GravitonRequest request = builder.head().build();
        assertEquals(HttpMethod.HEAD, request.getMethod());
        assertNull(request.getBody());
    }

    @Test
    public void testOptions() throws Exception {
        GravitonRequest request = builder.options().build();
        assertEquals(HttpMethod.OPTIONS, request.getMethod());
        assertNull(request.getBody());
    }

    @Test
    public void testMultipartPost() throws Exception {
        String formName = "something";
        String body1 = "body part 1";
        String body2 = "body part 2";
        Part part1 = new Part(body1, formName);
        Part part2 = new Part(body2);

        GravitonRequest request = builder.post(part1, part2).build();
        assertEquals(HttpMethod.POST, request.getMethod());
        List<Part> parts = request.getParts();
        assertEquals(2, parts.size());
        assertEquals(part1, parts.get(0));
        assertEquals(part2, parts.get(1));
    }

    @Test
    public void testMultipartPut() throws Exception {
        String body = "body part ";
        Part part = new Part(body);

        GravitonRequest request = builder.put(part).build();
        assertEquals(HttpMethod.PUT, request.getMethod());
        List<Part> parts = request.getParts();
        assertEquals(1, parts.size());
        assertEquals(part, parts.get(0));
    }

    @Test
    public void testParams() throws Exception {
        String param1 = "param1";
        String param2 = "param2";
        String value1 = "value1";
        String value2 = "value2";
        assertEquals(0, builder.getParams().size());
        builder.addParam(param1, value1);
        assertEquals(1, builder.getParams().size());
        assertEquals(value1, builder.getParams().get(param1));

        builder.addParam(param2, value1);
        assertEquals(2, builder.getParams().size());
        assertEquals(value1, builder.getParams().get(param2));

        Map<String, String> params = new HashMap<>();
        params.put(param2, value2);
        builder.setParams(params);
        assertEquals(1, builder.getParams().size());
        assertEquals(value2, builder.getParams().get(param2));
    }

    @Test
    public void testHeaders() throws Exception {
        String param1 = "param1";
        String param2 = "param2";
        String value1 = "value1";
        String value2 = "value2";
        GravitonRequest request = builder.build();
        assertEquals(2, request.getHeaders().all().size());
        assertEquals("application/json", request.getHeaders().get("Content-Type").get(0));
        assertEquals("application/json", request.getHeaders().get("Accept").get(0));
        request = builder.addHeader(param1, value1).build();
        assertEquals(3, request.getHeaders().all().size());
        assertEquals(value1, request.getHeaders().get(param1).get(0));

        request = builder.addHeader(param2, value2).build();
        assertEquals(4, request.getHeaders().all().size());
        assertEquals(value2, request.getHeaders().get(param2).get(0));

        request = builder.setHeaders(null).build();
        assertEquals(0, request.getHeaders().all().size());
    }
}