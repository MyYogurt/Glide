package org.moisiadis.glide.util.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spawns with each new HTTP request. Parses request and returns HTTPRequest for user to use
 */
public class ServerThread implements Callable<HTTPRequest> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final Socket socket;

    private HTTPExchangeErrorHandler errorHandler;

    /**
     * Spawn standard server thread
     *
     * @param socket Socket to operate on
     */
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Spawn server thread with optional error handler
     *
     * @param socket       Socket to operate on
     * @param errorHandler Optional custom error handler
     */
    public ServerThread(Socket socket, HTTPExchangeErrorHandler errorHandler) {
        this.socket = socket;
        this.errorHandler = errorHandler;
    }

    /**
     * Parses HTTP request
     *
     * @return HTTPRequest object with information about the request.
     */
    @Override
    public HTTPRequest call() {
        int contentLength = -1;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder request = new StringBuilder();

            String line = bufferedReader.readLine();
            while (line != null && !line.isEmpty()) {
                if (line.contains("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                }
                request.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }

            StringBuilder payload = null;

            if (contentLength != -1) {
                payload = new StringBuilder();
                for (int i = 0; i < contentLength; i++) {
                    line = bufferedReader.readLine();
                    i += line.length();
                    payload.append(line).append("\r\n");
                }
            }

            StringBuilder requestMethod = new StringBuilder();

            char ch = request.charAt(0);
            int counter = 0;

            while (ch != ' ') {
                requestMethod.append(ch);
                counter++;
                ch = request.charAt(counter);
            }

            counter++;

            StringBuilder requestPath = new StringBuilder();

            ch = request.charAt(counter);

            while (ch != ' ') {
                requestPath.append(ch);
                counter++;
                ch = request.charAt(counter);
            }

            if (payload != null)
                return new HTTPRequest(requestMethod.toString(), requestPath.toString(), request.toString(), socket, payload.toString().getBytes());
            return new HTTPRequest(requestMethod.toString(), requestPath.toString(), request.toString(), socket);
        } catch (IOException e) {
            if (errorHandler != null) {
                errorHandler.handleIOException(e);
            } else {
                logger.log(Level.WARNING, "Exception in handling HTTP request.", e);
            }
        } catch (IndexOutOfBoundsException e) {
            if (errorHandler != null) {
                errorHandler.handleIndexOutOfBoundsException(e);
            } else {
                try {
                    HTTPResponseWriter.sendResponse(socket.getOutputStream(), 400);
                } catch (IOException ioException) {
                    logger.log(Level.WARNING, "Exception in sending Error response.", ioException);
                }
            }
        }
        return null;
    }
}