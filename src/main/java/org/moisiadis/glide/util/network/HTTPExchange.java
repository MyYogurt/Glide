package org.moisiadis.glide.util.network;

import java.io.IOException;

/**
 * Interface to create custom handling of requests
 */
public interface HTTPExchange {
    /**
     * Custom implementation of handling the HTTP request
     *
     * @param request HTTP Request
     */
    void handle(HTTPRequest request) throws IOException;
}
