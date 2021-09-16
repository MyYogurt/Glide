package org.moisiadis.glide.exceptions;

/**
 * Occurs if server receives an invalid HTTP request
 */
public class InvalidHTTPRequestException extends Exception {
    public InvalidHTTPRequestException() {
        super("Invalid HTTP request");
    }
}
