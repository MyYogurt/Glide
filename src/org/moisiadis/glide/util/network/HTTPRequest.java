package org.moisiadis.glide.util.network;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Class containing information about an HTTP request
 */
public class HTTPRequest {
    private final String requestMethod, requestPath, requestHeaders;
    private byte[] payload;

    private final Socket socket;

    /**
     * Create standard HTTPRequest object
     *
     * @param requestMethod  HTTP request method. (Example: GET, POST)
     * @param requestPath    HTTP request path. (Example: /docs/index.html
     * @param requestHeaders Any other headers included with the request.
     * @param socket         The socket the request came on.
     */
    public HTTPRequest(final String requestMethod, final String requestPath, final String requestHeaders, final Socket socket) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeaders = requestHeaders;
        this.socket = socket;
    }

    /**
     * Create HTTPRequest object with payload
     *
     * @param requestMethod  HTTP request method. (Example: GET, POST)
     * @param requestPath    HTTP request path. (Example: /docs/index.html
     * @param requestHeaders Any other headers included with the request.
     * @param socket         The socket the request came on.
     * @param hasPayload     HTTP request payload
     */
    public HTTPRequest(final String requestMethod, final String requestPath, final String requestHeaders, final Socket socket, final byte[] hasPayload) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeaders = requestHeaders;
        this.socket = socket;
        this.payload = payload;
    }

    public boolean hasPayload() {
        return payload != null;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    /**
     * @return All heads that came with the request
     */
    public String getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * @return payload that came with the HTTP request
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Send response with just a code
     *
     * @param code HTTP Response code
     */
    public void sendResponse(final int code) throws IOException {
        HTTPResponseWriter.sendResponse(socket.getOutputStream(), code);
    }

    /**
     * Send response with data
     *
     * @param code        HTTP Response code
     * @param contentType Mime type of data
     * @param payload     Data to be sent
     */
    public void sendResponse(final int code, final String contentType, final byte[] payload) throws IOException {
        HTTPResponseWriter.sendResponse(socket.getOutputStream(), code, payload);
    }

    /**
     * Send response with file
     *
     * @param code        HTTP Response code
     * @param contentType Mime type of file
     * @param file        File to be sent
     */
    public void sendResponse(final int code, final String contentType, final File file) throws IOException {
        HTTPResponseWriter.sendResponse(socket.getOutputStream(), code, file);
    }
}