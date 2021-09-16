package org.moisiadis.glide;

import java.io.File;
import java.io.IOException;

import org.moisiadis.glide.util.network.HTTPExchange;
import org.moisiadis.glide.util.network.HTTPExchangeErrorHandler;
import org.moisiadis.glide.util.network.HTTPRequest;

public class Main {
    public static void main(String[] args) {
        Glide glide = new Glide(80, 4);
        glide.setContext("/", new Handler());
        glide.setContext("/test", new Test());
        glide.setContext("/test/test", new TestTest());
        glide.setErrorHandler(new ErrorHandler());
        try {
            glide.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Handler implements HTTPExchange {
    @Override
    public void handle(HTTPRequest request) {
        File file;
        if (request.getRequestPath().equals("/")) {
            file = new File("html/index.html");
        } else {
            file = new File("html" + request.getRequestPath());
        }

        if (!file.exists()) {
            try {
                request.sendResponse(404);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            final String mimeType = "text/html";
            request.sendResponse(200, mimeType, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Test implements HTTPExchange {
    @Override
    public void handle(HTTPRequest request) {
        String message = "Success";
        try {
            request.sendResponse(200, "text/plain", message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TestTest implements HTTPExchange {
    @Override
    public void handle(HTTPRequest request) {
        String message = "SuccessSuccess";
        try {
            request.sendResponse(200, "text/plain", message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ErrorHandler implements HTTPExchangeErrorHandler {

    @Override
    public void handleIOException(IOException ioException) {
        ioException.printStackTrace();
    }

    @Override
    public void handleIndexOutOfBoundsException(IndexOutOfBoundsException indexOutOfBoundsException) {
        indexOutOfBoundsException.printStackTrace();
    }
}