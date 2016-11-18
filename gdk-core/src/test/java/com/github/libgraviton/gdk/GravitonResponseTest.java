package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.data.NoopClass;
import com.github.libgraviton.gdk.data.SerializationTestClass;
import com.github.libgraviton.gdk.exception.SerializationException;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class GravitonResponseTest {

    private GravitonResponse gravitonResponse;

    @Before
    public void setup() throws Exception {
        gravitonResponse = new GravitonResponse.Builder(mock(GravitonRequest.class))
                .body("{\"code\":0}")
                .build();
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
