package com.comviva.hceservice.tds;

/**
 * UI listener for TDS unregister.
 */
public interface UnregisterTdsListener {
    /**
     * UnRegister is started.
     */
    void onStarted();

    /**
     * Error Occurred.
     * @param message Error Message
     */
    void onError(String message);

    /**
     * TDS Unregister is successful.
     */
    void onSuccess();
}
