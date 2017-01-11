package com.github.libgraviton.gdk.exception;

import com.github.libgraviton.gdk.api.GravitonResponse;

/**
 * Whenever a received response code is not within 200 - 299.
 */
public class UnsuccessfulResponseException extends CommunicationException {

    private GravitonResponse response;

    public UnsuccessfulResponseException(GravitonResponse response) {
        super(generateMessage(response));
        this.response = response;
    }

    public UnsuccessfulResponseException(String message) {
        super(message);
    }

    public UnsuccessfulResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public GravitonResponse getResponse() {
        return response;
    }

    private static String generateMessage(GravitonResponse response) {
        return String.format(
                "Failed '%s' to '%s'. Response was '%d' - '%s' with body '%s'.",
                response.getRequest().getMethod(),
                response.getRequest().getUrl(),
                response.getCode(),
                response.getMessage(),
                response.getBodyItem()
        );
    }

}
