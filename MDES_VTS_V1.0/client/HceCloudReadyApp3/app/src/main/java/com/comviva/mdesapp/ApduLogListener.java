package com.comviva.mdesapp;


public interface ApduLogListener {
    void onCommandApduReceived(String commandApdu);

    void onResponseApdu(String responseApdu);

    void onDeactivated(String log);
}
