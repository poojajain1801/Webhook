package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * UI Listener for Card Life Cycle Management Operations.
 */
public interface CardLcmListener {
    /**
     * Card Life Cycle Management Operation started.
     */
    void onCardLcmStarted();

    /**
     * Card Life Cycle Management Operation successful.
     * @param message
     */
    void onSuccess(String message);

    /**
     * Error Occurred.
     * @param message Error Message
     */
    void onError(String message);
}
