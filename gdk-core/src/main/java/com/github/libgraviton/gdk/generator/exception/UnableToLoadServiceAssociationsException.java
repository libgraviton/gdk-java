package com.github.libgraviton.gdk.generator.exception;

public class UnableToLoadServiceAssociationsException extends Exception {

    public UnableToLoadServiceAssociationsException(String message) {
        super(message);
    }

    public UnableToLoadServiceAssociationsException(String message, Exception e) {
        super(message, e);
    }

}
