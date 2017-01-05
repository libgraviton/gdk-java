package com.github.libgraviton.gdk.exception;

/**
 * Whenever a call to the backend was not successful.
 */
public class CommunicationException extends Exception {

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
