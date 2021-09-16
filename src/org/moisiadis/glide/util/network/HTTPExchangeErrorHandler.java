package org.moisiadis.glide.util.network;

import java.io.IOException;

/**
 * Allows for custom handling of errors thrown during processing of HTTP request.
 */
public interface HTTPExchangeErrorHandler {
    /**
     * Handle IOException when processing HTTP request. May be caused by getInputStream(), readLine(), etc.
     * @param ioException exception from processing HTTP request.
     */
    void handleIOException(IOException ioException);

    /**
     * Handle IndexOutOfBoundsException when processing HTTP request.
     * @param indexOutOfBoundsException excpetion from processing HTTP request.
     */
    void handleIndexOutOfBoundsException(IndexOutOfBoundsException indexOutOfBoundsException);
}
