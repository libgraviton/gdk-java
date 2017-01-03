package com.github.libgraviton.gdk.exception;

import okhttp3.Response;

import java.io.IOException;

/**
 * Whenever a call to the backend was not successful.
 */
public class CommunicationException extends Exception {


    private Response response;

    public CommunicationException() {
        super();
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(Response response) {
        super(response.toString());
        this.response = response;
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public int getResponseCode() {
        return response.code();
    }

    public String getResponseBody() {
        try {
            return response.body().string();
        } catch (IOException e) {
            return "Unable to fetch response setBody. " + e.getMessage();
        }
    }
}
