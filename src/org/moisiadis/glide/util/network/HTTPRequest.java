package org.moisiadis.glide.util.network;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class HTTPRequest {
    private final String requestMethod, requestPath, requestHeaders;
    private byte[] payload;

    private final Socket socket;

    public HTTPRequest (final String requestMethod, final String requestPath, final String requestHeaders, final Socket socket) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeaders = requestHeaders;
        this.socket = socket;
    }

    public HTTPRequest (final String requestMethod, final String requestPath, final String requestHeaders, final Socket socket, final byte[] hasPayload) {
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

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void sendResponse(final int code) throws IOException {
        HTTPResponseWriter.sendResponse(socket.getOutputStream(), code);
    }

    public void sendResponse(final int code, final String contentType, final byte[] payload) throws IOException {
        HTTPResponseWriter.sendResponse(socket.getOutputStream(), code, payload);
    }

    public void sendResponse(final int code, final String contentType, final File file) throws IOException {
        HTTPResponseWriter.sendResponse(socket.getOutputStream(), code, file);
    }
}