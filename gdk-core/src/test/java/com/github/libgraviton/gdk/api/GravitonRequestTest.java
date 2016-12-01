package com.github.libgraviton.gdk.api;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class GravitonRequestTest {

    private GravitonRequest.Builder builder;

    @Before
    public void setup() throws Exception {
        builder = new GravitonRequest.Builder().setUrl("http://aRandomUrl");
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
        assertEquals(0, request.getHeaders().all().size());
        request = builder.addHeader(param1, value1).build();
        assertEquals(1, request.getHeaders().all().size());
        assertEquals(value1, request.getHeaders().get(param1).get(0));

        request = builder.addHeader(param2, value2).build();
        assertEquals(2, request.getHeaders().all().size());
        assertEquals(value2, request.getHeaders().get(param2).get(0));

        request = builder.setHeaders(null).build();
        assertEquals(0, request.getHeaders().all().size());
    }
}
