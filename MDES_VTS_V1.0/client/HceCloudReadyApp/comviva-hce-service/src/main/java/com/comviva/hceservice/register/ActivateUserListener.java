package com.comviva.hceservice.register;


import com.comviva.hceservice.common.RestResponse;

public interface ActivateUserListener {
    void onActivationStarted();

    void onActivationCompeted();

    void onError(String errorMessage);
}
