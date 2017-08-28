package com.comviva.hceservice.register;

/**
 * UI listener for Register Device & Activate user.
 */
public interface RegistrationListener {
    /**
     * Task is started. Application can start any UI operation e.g. showing progress dialog on this event.
     */
    void onStarted();

    /**
     * Task is completed.
     */
    void onCompeted();

    /**
     * Error occurred while executing the task.
     * @param errorMessage  Error Message
     */
    void onError(String errorMessage);
}
