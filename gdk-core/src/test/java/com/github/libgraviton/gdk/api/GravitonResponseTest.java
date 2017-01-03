package com.github.libgraviton.gdk.api;

import com.github.libgraviton.gdk.data.NoopClass;
import com.github.libgraviton.gdk.data.SerializationTestClass;
import com.github.libgraviton.gdk.exception.SerializationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class GravitonResponseTest {

    private GravitonResponse response;

    private GravitonRequest request;

    @Before
    public void setup() throws Exception {
        request = mock(GravitonRequest.class);
        response = new GravitonResponse.Builder(request)
                .body("{\"code\":0}")
                .successful(true)
                .message("a message")
                .code(200)
                .build();
    }

    @Test(expected = SerializationException.class)
    public void testDeserializeBodyWithException() throws SerializationException {
        response.getBody(NoopClass.class);
    }

    @Test
    public void testSuccessfulDeserializeBody() throws SerializationException {
        response.getBody(SerializationTestClass.class);
        assertTrue(response.isSuccessful());
        assertEquals(200, response.getCode());
        assertEquals("a message", response.getMessage());
        assertEquals("{\"code\":0}", response.getBody());
        assertEquals(request, response.getRequest());
        assertEquals(0, response.getHeaders().all().size());
    }
}
