package com.comviva.hceservice.digitizationApi;


import com.comviva.hceservice.internalSdkListeners.CommonListener;

interface ConfirmProvisionListener extends CommonListener {
    void onCompleted();
}
