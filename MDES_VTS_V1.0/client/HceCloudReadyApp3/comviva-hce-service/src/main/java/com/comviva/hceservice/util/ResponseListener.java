package com.comviva.hceservice.util;

import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.digitizationApi.CardMetaData;

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
     * @param sdkError Error
     */
    void onError(SdkError sdkError);
}
