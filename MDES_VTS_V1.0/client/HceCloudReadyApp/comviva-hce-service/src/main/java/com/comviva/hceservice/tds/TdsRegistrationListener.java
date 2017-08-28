package com.comviva.hceservice.tds;

/**
 * UI Listener for TDS registration.
 */
public interface TdsRegistrationListener {
    /**
     * TDS registration started.
     */
    void onRegistrationStarted();

    /**
     * Error occurred.
     * @param message Error Message
     */
    void onError(String message);

    /**
     * Registration is successful
     */
    void onSuccess();
}
