package com.github.libgraviton.gdk.header;

import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.api.header.LinkHeader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeaderBagTest {

    @Test
    public void testLink() {
        String inputLink = "<http://localhost:8000/some/graviton/endpoint/1234>; rel=\"self\",<http://localhost:8000/event/status/20c3b1f9c3b83d339bd88e8e5b0d7066>; rel=\"eventStatus\"";
        String expectedLink = "http://localhost:8000/event/status/20c3b1f9c3b83d339bd88e8e5b0d7066";
        HeaderBag headers = new HeaderBag.Builder()
                .set("Link", inputLink)
                .build();

        assertEquals(expectedLink, headers.getLink(LinkHeader.EVENT_STATUS));
    }

}
