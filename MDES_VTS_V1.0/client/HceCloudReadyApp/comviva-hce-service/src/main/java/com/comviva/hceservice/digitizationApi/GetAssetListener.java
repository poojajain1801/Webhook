package com.comviva.hceservice.digitizationApi;

public interface GetAssetListener {
    /**
     *
     */
    void onStarted();

    /**
     *
     */
    void onCompleted(ContentGuid contentGuid);

    /**
     * Error Occurred.
     * @param message Error message
     */
    void onError(String message);
}
