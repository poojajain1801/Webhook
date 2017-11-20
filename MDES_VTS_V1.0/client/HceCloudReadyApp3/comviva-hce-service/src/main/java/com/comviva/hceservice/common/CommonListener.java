package com.comviva.hceservice.common;

public interface CommonListener {
    /**
     * Task is started. Application can start any UI operation e.g. showing progress dialog on this event.
     */
    void onStarted();

    /**
     * Error occurred while executing the task.
     * @param sdkError  Error
     */
    void onError(SdkError sdkError);
}
