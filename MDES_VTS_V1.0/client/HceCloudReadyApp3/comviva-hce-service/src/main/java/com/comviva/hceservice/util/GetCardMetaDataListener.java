package com.comviva.hceservice.util;

import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.digitizationApi.CardMetaData;

/**
 * Created by amit.randhawa on 1/8/2018.
 */

public interface GetCardMetaDataListener {

    void onStarted();

    /**
     * Task successful.
     */
    void onSuccess(CardMetaData cardMetaData);

    /**
     * Error while performing task.
     *
     * @param sdkError Error
     */
    void onError(SdkError sdkError);
}
