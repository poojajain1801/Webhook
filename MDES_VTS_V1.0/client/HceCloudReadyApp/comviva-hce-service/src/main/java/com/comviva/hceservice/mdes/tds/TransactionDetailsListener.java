package com.comviva.hceservice.mdes.tds;

import java.util.ArrayList;

public interface TransactionDetailsListener {
    void onStarted();

    void onError(String message);

    void onSuccess(ArrayList<TransactionDetails> transactionDetails);
}
