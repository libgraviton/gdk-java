package com.github.libgraviton.gdk.api.gateway;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.exception.CommunicationException;

public interface GravitonGateway {

    GravitonResponse execute(GravitonRequest request) throws CommunicationException;

}
