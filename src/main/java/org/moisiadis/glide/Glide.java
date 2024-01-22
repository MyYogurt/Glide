package org.moisiadis.glide;

import org.moisiadis.glide.exceptions.NoContextException;
import org.moisiadis.glide.util.network.HTTPExchange;
import org.moisiadis.glide.util.network.HTTPExchangeErrorHandler;
import org.moisiadis.glide.util.network.HTTPRequest;
import org.moisiadis.glide.util.network.HTTPResponseWriter;
import org.moisiadis.glide.util.network.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lightweight Java HTTP Library
 */
public class Glide {
    private final int port, threadCount;

    private HTTPExchangeErrorHandler errorHandler;

    private ServerSocket serverSocket;

    private final Map<String, HTTPExchange> contexts = new HashMap<String, HTTPExchange>();

    private static final Logger logger = Logger.getLogger(Glide.class.getName());

    /**
     * Create a new single threaded Glide server
     * @param port Port to operate on
     */
    public Glide(final int port) {
        this.port = port;
        threadCount = 1;
    }

    /**
     * Create new Glide server
     *
     * @param port        Port to operate on
     * @param threadCount Number of threads to use
     */
    public Glide(final int port, final int threadCount) {
        this.port = port;
        this.threadCount = threadCount;
    }

    public Glide(final int port, final int HTTPSPort, final int threadCount) {
        this.port = port;
        this.threadCount = threadCount;
    }

    /**
     * A root context ("/") is required.
     *
     * @param context  Path/Context
     * @param exchange Custom implementation of handle()
     */
    public void setContext(String context, HTTPExchange exchange) {
        contexts.put(context, exchange);
    }

    /**
     * @param errorHandler Custom error handler
     */
    public void setErrorHandler(HTTPExchangeErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    //TODO Optimize
    private HTTPExchange getContext(String path) {
        String altPath = path.substring(0, path.lastIndexOf('/'));
        int max = -1;
        HTTPExchange exchange = contexts.get("/");
        for (String key : contexts.keySet()) {
            if (key.contains(path) || key.equals(altPath)) {
                if (key.equals(path) || key.equals(altPath))
                    return contexts.get(key);
                if (key.length() > max) {
                    max = key.length();
                    exchange = contexts.get(key);
                }
            }
        }
        return exchange;
    }

    /**
     * Start Glide server
     *
     * @throws IOException        May occur when using sockets
     * @throws NoContextException Occurs if no contexts have been set
     */
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
