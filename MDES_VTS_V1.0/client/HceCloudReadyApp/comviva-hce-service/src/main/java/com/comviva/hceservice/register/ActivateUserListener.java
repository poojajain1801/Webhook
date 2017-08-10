package com.comviva.hceservice.register;

/**
 * UI listener for Activate user.
 */
public interface ActivateUserListener {
    /**
     * User activation started.
     */
    void onActivationStarted();

    /**
     * User is activated successfully.
     */
    void onActivationCompeted();

    /**
     * Error occurred while activation.
     * @param errorMessage  Error Message
     */
    void onError(String errorMessage);
}
