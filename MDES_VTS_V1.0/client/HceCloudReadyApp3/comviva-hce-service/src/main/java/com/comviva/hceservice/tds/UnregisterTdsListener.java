package com.comviva.hceservice.tds;

import com.comviva.hceservice.internalSdkListeners.CommonListener;

/**
 * UI listener for TDS unregister.
 */
public interface UnregisterTdsListener extends CommonListener {
    /**
     * TDS Unregister is successful.
     */
    void onSuccess();
}
