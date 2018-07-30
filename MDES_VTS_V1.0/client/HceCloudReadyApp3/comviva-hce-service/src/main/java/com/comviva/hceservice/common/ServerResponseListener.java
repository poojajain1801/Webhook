package com.comviva.hceservice.common;

/**
 * Created by amit.randhawa on 23/7/2018.
 */


public interface ServerResponseListener {
    /**
     * Called when the request is successfully completed and returns the validated response.
     *
     * @param result Result Object which needs to be casted to specific class as required
     */
    void onRequestCompleted(Object result, Object listener);

    /**
     * Called when unexpected error occurs.
     *
     * @param message Error description
     */
    void onRequestError(String message, Object listener);

}