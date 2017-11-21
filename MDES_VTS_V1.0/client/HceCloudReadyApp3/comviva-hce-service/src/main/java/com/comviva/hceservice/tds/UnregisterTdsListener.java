package com.comviva.hceservice.tds;

import com.comviva.hceservice.common.CommonListener;

/**
 * UI listener for TDS unregister.
 */
public interface UnregisterTdsListener extends CommonListener {
    /**
     * TDS Unregister is successful.
     */
    void onSuccess();
}
