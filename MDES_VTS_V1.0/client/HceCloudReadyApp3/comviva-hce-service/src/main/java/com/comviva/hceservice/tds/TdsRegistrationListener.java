package com.comviva.hceservice.tds;

import com.comviva.hceservice.internalSdkListeners.CommonListener;

/**
 * UI Listener for TDS registration.
 */
public interface TdsRegistrationListener extends CommonListener {
    /**
     * Registration is successful
     */
    void onSuccess();
}
