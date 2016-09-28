package com.github.libgraviton.gdk.generator.exception;

/**
 * Created by tgdpaad2 on 28/09/16.
 */
public class GeneratorException extends RuntimeException {

    public GeneratorException(String message, Exception e) {
        super(message, e);
    }

}
