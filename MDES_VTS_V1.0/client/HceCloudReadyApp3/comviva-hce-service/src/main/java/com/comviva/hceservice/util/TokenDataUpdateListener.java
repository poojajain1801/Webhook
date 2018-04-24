package com.comviva.hceservice.util;

import com.comviva.hceservice.common.SdkError;

/**
 * Created by amit.randhawa on 23-04-2018.
 */

public interface TokenDataUpdateListener {

    void onSuccess(String newStatus);
    void onError(SdkError sdkError);
    void onStarted();
}
