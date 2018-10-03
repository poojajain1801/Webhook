package com.comviva.hceservice.listeners;

import com.comviva.hceservice.internalSdkListeners.CommonListener;

/**
 * Created by amit.randhawa on 1/8/2018.
 */

public interface TokenDataUpdateListener extends CommonListener {


    /**
     * Task successful.
     */
    void onSuccess(String newString);


}
