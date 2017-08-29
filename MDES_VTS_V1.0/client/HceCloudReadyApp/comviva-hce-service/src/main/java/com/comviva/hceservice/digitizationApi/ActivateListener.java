package com.comviva.hceservice.digitizationApi;

/**
 * UI Listener for Activate Token.
 */
public interface ActivateListener {
    /**
     * Activation started.
     */
    void onActivationStarted();

    /**
     * Error occurred.
     * @param message   Error Message
     */
    void onError(String message);

    /** Activation was successful */
    void onSuccess();

    /** Authentication Code was incorrect and rejected. Retries permitted */
    void onIncorrectCode();

    /** Authentication Code was incorrect and the maximum number of retries now exceeded. */
    void onRetriesExceeded();

    /**  Authentication Code has expired or was invalidated */
    void onExpiredCode();

    /** Tokenization Authentication Value was incorrect and rejected. */
    void onIncorrectTAV();

    /** The Token cannot be activated because the digitization session has expired.
     * The caller must delete any artifacts relating to the Token and restart the process from the beginning.*/
    void onSessionExpired();
}