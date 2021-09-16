package org.moisiadis.glide.util.network;

/**
 * Interface to create custom handling of requests
 */
public interface HTTPExchange {
    /**
     * Custom implementation of handling the HTTP request
     * @param request HTTP Request
     */
    void handle(HTTPRequest request);
}
