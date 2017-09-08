package com.comviva.hceservice.util;

/**
 * Generic listener.
 */
public interface ResponseListener {
    /**
     * Task started.
     */
    void onStarted();

    /**
     * Task successful.
     */
    void onSuccess();

    /**
     * Error while performing task.
     *
     * @param error Error Message
     */
    void onError(String error);
}
