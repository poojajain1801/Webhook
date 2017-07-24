package com.comviva.hceservice.mdes.tds;


public interface UnregisterTdsListener {
    void onStarted();

    void onError(String message);

    void onSuccess();
}
