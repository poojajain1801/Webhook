package com.comviva.hceservice.mdes.digitizatioApi;

public interface RequestActivationCodeListener {
    void onReqActivationCodeStarted();

    void onSuccess(String message);

    void onError(String message);
}
