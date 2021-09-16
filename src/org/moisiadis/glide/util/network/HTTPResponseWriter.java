package org.moisiadis.glide.util.network;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HTTPResponseWriter {

    public static void sendResponse(OutputStream outputStream, final int code) {
        final String response;
        if (code == 200) {
            response = "HTTP/1.1 " + code  + " OK\r\n\r\n";
        } else {
            response = "HTTP/1.1 " + code  + "\r\n\r\n";
        }
        try {
            outputStream.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendResponse(OutputStream outputStream, final int code, final byte[] responseBody) {
        final String response;
        if (code == 200) {
            response = "HTTP/1.1 " + code  + " OK\r\n";
        } else {
            response = "HTTP/1.1 " + code  + "\r\n";
        }
        try {
            outputStream.write(response.getBytes());
            if (responseBody != null) {
                final String contentLength = "Content-Length: "+ responseBody.length + "\r\n\r\n";
                outputStream.write(contentLength.getBytes());
                outputStream.write(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendResponse(OutputStream outputStream, final int code, final File file) {
        final String response;
        if (code == 200) {
            response = "HTTP/1.1 " + code  + " OK\r\n";
        } else {
            response = "HTTP/1.1 " + code  + "\r\n";
        }
        try {
            outputStream.write(response.getBytes());
            final String contentLength = "Content-Length: " + file.length() + "\r\n\r\n";
            outputStream.write(contentLength.getBytes());
            Files.copy(file.toPath(), outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}