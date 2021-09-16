package org.moisiadis.glide.exceptions;

public class NoContextException extends Exception {
    public NoContextException() {
        super("No context provided for request");
    }
}
