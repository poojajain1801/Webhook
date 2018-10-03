package com.comviva.hceservice.common;


public interface ProcessContactlessListener  {
    void onContactlessReady();

    void onContactlessPaymentCompleted(String var1);

    void onContactlessPaymentAborted(String var1);

    void onPinRequired(String var1);
}