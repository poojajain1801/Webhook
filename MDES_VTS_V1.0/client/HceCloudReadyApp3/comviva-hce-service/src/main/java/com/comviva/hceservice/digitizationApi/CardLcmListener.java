package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CommonListener;

/**
 * UI Listener for Card Life Cycle Management Operations.
 */
public interface CardLcmListener extends CommonListener {
    /**
     * Card Life Cycle Management Operation successful.
     * @param message message
     */
    void onSuccess(String message);
}
