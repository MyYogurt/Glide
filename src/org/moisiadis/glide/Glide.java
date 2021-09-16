package org.moisiadis.glide;

import org.moisiadis.glide.exceptions.NoContextException;
import org.moisiadis.glide.util.network.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Glide {
    private final int port, threadCount;

    private boolean hasRootContextSet;

    private ServerSocket serverSocket;

    private HTTPExchangeErrorHandler errorHandler;

    private final HashMap<String, HTTPExchange> contexts = new HashMap<String, HTTPExchange>();
    
    private static final Logger logger = Logger.getLogger(Glide.class.getName());

    public Glide(final int port, final int threadCount) {
        this.port = port;
        this.threadCount = threadCount;
    }

    public void setContext(String context, HTTPExchange exchange) {
        if (!hasRootContextSet && context.equals("/"))
            hasRootContextSet = true;
        contexts.put(context, exchange);
    }

    public void setErrorHandler(HTTPExchangeErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    //TODO Optimize
    private HTTPExchange getContext(String path) {
        int max = -1;
        HTTPExchange exchange = contexts.get("/");
        for (String key : contexts.keySet()) {
            if (key.contains(path) || key.equals(path.substring(0, path.lastIndexOf('/')))) {
                if (key.equals(path) || key.equals(path.substring(0, path.lastIndexOf('/'))))
                    return contexts.get(key);
                if (key.length() > max) {
                    max = key.length();
                    exchange = contexts.get(key);
                }
            }
        }
        return exchange;
    }
    
    public void start() throws IOException, NoContextException {
        if (contexts.isEmpty()) {
            logger.log(Level.SEVERE, "No contexts provided. Server cannot run.");
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        serverSocket = new ServerSocket(port);

        while (true) {
            final Socket socket = serverSocket.accept();
            final Future<HTTPRequest> serverThread;
            if (errorHandler != null) {
                serverThread = executor.submit(new ServerThread(socket, errorHandler));
            } else {
                serverThread = executor.submit(new ServerThread(socket));
            }
            new Thread(() -> {
                HTTPRequest request;
                try {
                    request = serverThread.get();
                    HTTPExchange exchange = getContext(request.getRequestPath());
                    if (exchange != null) {
                        exchange.handle(request);
                    } else {
                        logger.log(Level.WARNING, "No context for request. Sending error code.");
                        HTTPResponseWriter.sendResponse(socket.getOutputStream(), 400);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Exception in handling HTTPRequest.", e);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Exception in closing socket.", e);
                }
            }).start();
        }
    }
}
