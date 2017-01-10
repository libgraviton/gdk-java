package com.github.libgraviton.gdk.exception;

public class NoCorrespondingEndpointException extends IllegalStateException {

    public NoCorrespondingEndpointException(String className) {
        super("Could not find an endpoint corresponding to POJO class '" + className + "'.");
    }

}
