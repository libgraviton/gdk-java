package com.github.libgraviton.gdk.exception;

public class NoCorrespondingServiceException extends Exception {

    public NoCorrespondingServiceException(String className) {
        super("Could not find an endpoint corresponding to POJO class '" + className + "'.");
    }

}
