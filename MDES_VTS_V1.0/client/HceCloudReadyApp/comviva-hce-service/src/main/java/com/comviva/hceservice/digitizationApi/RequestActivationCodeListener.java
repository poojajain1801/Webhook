package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CommonListener;

/**
 * UI Listener for Activation API.
 */
public interface RequestActivationCodeListener extends CommonListener {
    /**
     * Activation Code Successful
     * @param message Response of Activation Code Request
     */
    void onSuccess(String message);
}
