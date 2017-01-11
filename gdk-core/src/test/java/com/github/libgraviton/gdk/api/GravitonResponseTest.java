package com.github.libgraviton.gdk.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.data.NoopClass;
import com.github.libgraviton.gdk.data.SerializationTestClass;
import com.github.libgraviton.gdk.exception.DeserializationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
        response.setObjectMapper(new ObjectMapper());
    }

    @Test(expected = DeserializationException.class)
    public void testDeserializeBodyWithException() throws DeserializationException {
        response.getBodyItem(NoopClass.class);
    }

    @Test
    public void testSuccessfulDeserializeBody() throws DeserializationException {
        response.getBodyItem(SerializationTestClass.class);
        assertTrue(response.isSuccessful());
        assertEquals(200, response.getCode());
        assertEquals("a message", response.getMessage());
        assertEquals("{\"code\":0}", response.getBody());
        assertEquals(request, response.getRequest());
        assertEquals(0, response.getHeaders().all().size());
    }

    @Test
    public void testDeserializeBodyAsList() throws Exception {
        SerializationTestClass testClass1 = new SerializationTestClass();
        testClass1.setCode(1);
        SerializationTestClass testClass2 = new SerializationTestClass();
        testClass2.setCode(2);
        List<SerializationTestClass> testClasses = Arrays.asList(testClass1, testClass2);

        response = new GravitonResponse.Builder(request)
                .body(new ObjectMapper().writeValueAsString(testClasses))
                .successful(true)
                .message("a message")
                .code(200)
                .build();
        response.setObjectMapper(new ObjectMapper());

        response.getBodyItems(SerializationTestClass.class);
        assertTrue(response.isSuccessful());
        List<SerializationTestClass> items = response.getBodyItems(SerializationTestClass.class);
        assertEquals(2, items.size());
        assertEquals(testClass1.getCode(), items.get(0).getCode());
        assertEquals(testClass2.getCode(), items.get(1).getCode());
    }
}
