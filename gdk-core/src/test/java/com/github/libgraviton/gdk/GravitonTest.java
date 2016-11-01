package com.github.libgraviton.gdk;


import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.generator.GeneratedServiceManager;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GravitonTest {

    private Graviton graviton;

    private Request request;

    private Response response;

    private Call call;

    private ResponseBody body;

    private String responseBody = "some random response body";

    @Before
    public void setup() throws Exception {
        graviton = new Graviton("https://api.rel-pub.zgkb.evoja.ch", new GeneratedServiceManager(new File("noPathToFindHere")));
        OkHttpClient client = mock(OkHttpClient.class);
        graviton.setOkHttp(client);

        request = mock(Request.class);

        // mock okHttp.newCall(request).execute();
        call = mock(Call.class);
        response = mock(Response.class);
        when(call.execute()).thenReturn(response);
        when(client.newCall(request)).thenReturn(call);

        // mock response.body().string()
        body = mock(ResponseBody.class);
        when(body.string()).thenReturn(responseBody);
        when(response.body()).thenReturn(body);
        when(response.request()).thenReturn(request);
    }

    @Test
    public void testDoRequestHappyPath() throws CommunicationException {
        String method = "GET";
        String url = "someUrl";

        when(response.isSuccessful()).thenReturn(true);

        GravitonResponse response = graviton.doRequest(method, url, request);
        assertEquals(responseBody, response.getBody());
    }

    @Test(expected = CommunicationException.class)
    public void testDoRequestFailedCall() throws CommunicationException {
        String method = "GET";
        String url = "someUrl";

        try {
            when(call.execute()).thenThrow(new IOException("The call went wrong, but that's ok."));
        } catch (IOException e) {
            e.printStackTrace();
        }

        graviton.doRequest(method, url, request);
    }

    @Test(expected = CommunicationException.class)
    public void testDoRequestCorruptResponseBody() throws CommunicationException {
        String method = "GET";
        String url = "someUrl";

        try {
            when(body.string()).thenThrow(new IOException("Response body is corrupt!"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        when(request.method()).thenReturn("GET");
        HttpUrl httpUrl = mock(HttpUrl.class);
        when(httpUrl.toString()).thenReturn("someUrl");

        graviton.doRequest(method, url, request);
    }

    @Test(expected = CommunicationException.class)
    public void testUnsuccessfulResponse() throws CommunicationException {
        String method = "GET";
        String url = "someUrl";

        when(response.isSuccessful()).thenReturn(false);

        graviton.doRequest(method, url, request);
    }
}
