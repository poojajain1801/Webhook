package com.comviva.hceservice.mdes.tds;

public interface TransactionDetailsListener {
    void onStarted();

    void onError(String message);

    void onSuccess(TransactionDetails[] transactionDetails);
}
