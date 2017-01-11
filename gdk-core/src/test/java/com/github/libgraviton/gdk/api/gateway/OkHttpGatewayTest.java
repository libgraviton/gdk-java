package com.github.libgraviton.gdk.api.gateway;


import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.HttpMethod;
import com.github.libgraviton.gdk.exception.CommunicationException;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OkHttpGatewayTest {

    private OkHttpGateway gateway;

    private GravitonRequest request;

    private Response okHttpResponse;

    private Call call;

    private ResponseBody body;

    private String responseBody = "some random okHttpResponse setBody";

    @Before
    public void setup() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        gateway = new OkHttpGateway(client);
        okHttpResponse = mock(Response.class);
        when(okHttpResponse.headers()).thenReturn(new Headers.Builder().build());
        Graviton graviton = mock(Graviton.class);
        request = new GravitonRequest.Builder(graviton)
                .setMethod(HttpMethod.GET)
                .setUrl("http://someUrl")
                .build();

        // mock client.newCall()
        call = mock(Call.class);
        when(call.execute()).thenReturn(okHttpResponse);
        when(client.newCall(any(Request.class))).thenReturn(call);

        // mock okHttpResponse.setBody().string()
        body = mock(ResponseBody.class);
        when(body.string()).thenReturn(responseBody);
        when(okHttpResponse.body()).thenReturn(body);
    }

    @Test
    public void testDoRequestHappyPath() throws CommunicationException {
        when(okHttpResponse.isSuccessful()).thenReturn(true);

        GravitonResponse response = gateway.execute(request);
        assertEquals(responseBody, response.getBodyItem());
    }

    @Test(expected = CommunicationException.class)
    public void testDoRequestFailedCall() throws CommunicationException {
        try {
            when(call.execute()).thenThrow(new IOException("The call went wrong, but that's ok."));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gateway.execute(request);
    }

    @Test(expected = CommunicationException.class)
    public void testDoRequestCorruptResponseBody() throws CommunicationException {
        try {
            when(body.string()).thenThrow(new IOException("Response setBody is corrupt!"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gateway.execute(request);
    }

}
