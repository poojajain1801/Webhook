package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * UI Listener for Activation API.
 */
public interface RequestActivationCodeListener {
    /**
     * Request for Activation Code started
     */
    void onReqActivationCodeStarted();

    /**
     * Activation Code Successful
     * @param message Response of Activation Code Request
     */
    void onSuccess(String message);

    /**
     * Error occurred.
     * @param message   Error Message
     */
    void onError(String message);
}
