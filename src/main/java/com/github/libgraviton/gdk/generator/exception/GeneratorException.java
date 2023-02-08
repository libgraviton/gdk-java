package com.github.libgraviton.workerbase.gdk.generator.exception;

/**
 * Is thrown when the POJO generation fails.
 */
public class GeneratorException extends Exception {

    public GeneratorException(String message, Exception e) {
        super(message, e);
    }

}
