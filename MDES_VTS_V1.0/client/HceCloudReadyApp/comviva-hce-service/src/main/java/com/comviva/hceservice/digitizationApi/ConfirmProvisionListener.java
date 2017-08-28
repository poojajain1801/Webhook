package com.comviva.hceservice.digitizationApi;


interface ConfirmProvisionListener {
    void onCompleted();

    void onError(String errorMessage);
}
