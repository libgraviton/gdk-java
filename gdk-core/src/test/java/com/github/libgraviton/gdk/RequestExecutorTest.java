package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.api.HttpMethod;
import com.github.libgraviton.gdk.api.Request;
import com.github.libgraviton.gdk.api.Response;
import com.github.libgraviton.gdk.api.gateway.GravitonGateway;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.UnsuccessfulResponseException;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestExecutorTest {

    private RequestExecutor executor;

    private GravitonGateway gateway;

    @Before
    public void setup() {
        executor = new RequestExecutor();
        gateway = mock(GravitonGateway.class);
        executor.setGateway(gateway);
    }

    @Test
    public void testExecuteSuccessfulResponse() throws CommunicationException {
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        when(gateway.execute(eq(request))).thenReturn(response);

        Response actualResponse = executor.execute(request);
        assertEquals(response, actualResponse);
    }

    @Test(expected = UnsuccessfulResponseException.class)
    public void testExecuteUnsuccessfulResponse() throws Exception {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getUrl()).thenReturn(new URL("http://some-test-url"));
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getRequest()).thenReturn(request);
        when(response.getCode()).thenReturn(500);
        when(response.getMessage()).thenReturn("Oops!");
        when(response.getBody()).thenReturn("Content");
        when(gateway.execute(eq(request))).thenReturn(response);

        Response actualResponse = executor.execute(request);
        assertEquals(response, actualResponse);
    }
}
