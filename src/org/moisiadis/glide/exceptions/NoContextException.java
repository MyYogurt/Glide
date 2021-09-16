package org.moisiadis.glide.exceptions;

/**
 * Thrown if no contexts are provided by server startup
 */
public class NoContextException extends Exception {
    public NoContextException() {
        super("No context provided for request");
    }
}
