package com.comviva.hceservice.mdes.tds;

public interface TdsRegistrationListener {
    void onRegistrationStarted();

    void onError(String message);

    /** Registration was successful */
    void onSuccess();
}
