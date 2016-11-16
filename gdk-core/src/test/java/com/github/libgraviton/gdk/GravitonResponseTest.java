package com.github.libgraviton.gdk;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.data.NoopClass;
import com.github.libgraviton.gdk.data.SerializationTestClass;
import com.github.libgraviton.gdk.exception.SerializationException;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GravitonResponseTest {

    private GravitonResponse gravitonResponse;

    @Before
    public void setup() throws Exception {
        Response response = mock(Response.class);
        HttpUrl url = mock(HttpUrl.class);
        Request request = mock(Request.class);
        when(request.url()).thenReturn(url);
        when(response.request()).thenReturn(request);
        gravitonResponse = new GravitonResponse(response);
        gravitonResponse.setObjectMapper(new ObjectMapper());
        gravitonResponse.setBody("{\"code\":0}");
    }

    @Test
    public void testEventStatusLink() {
        String inputLink = "<http://localhost:8000/some/graviton/endpoint/1234>; rel=\"self\",<http://localhost:8000/event/status/20c3b1f9c3b83d339bd88e8e5b0d7066>; rel=\"eventStatus\"";
        String expectedLink = "http://localhost:8000/event/status/20c3b1f9c3b83d339bd88e8e5b0d7066";
        Headers headers = mock(Headers.class);
        when(headers.values("Link")).thenReturn(Arrays.asList(inputLink));
        when(gravitonResponse.getOriginalResponse().headers()).thenReturn(headers);
        assertEquals(expectedLink, gravitonResponse.getEventStatusLink());
    }

    @Test
    public void testLocation() {
        String location = "testLocation";
        when(gravitonResponse.getOriginalResponse().header("Location")).thenReturn(location);
        assertEquals(location, gravitonResponse.getLocation());
    }

    @Test(expected = SerializationException.class)
    public void testDeserializeBodyWithException() throws SerializationException {
        gravitonResponse.getBody(NoopClass.class);
    }

    @Test
    public void testSuccessfulDeserializeBody() throws SerializationException {
        gravitonResponse.getBody(SerializationTestClass.class);
    }
}
