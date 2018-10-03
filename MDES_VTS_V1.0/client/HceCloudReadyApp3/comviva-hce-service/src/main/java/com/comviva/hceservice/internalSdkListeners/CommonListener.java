package com.comviva.hceservice.internalSdkListeners;

import com.comviva.hceservice.common.SdkError;

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
