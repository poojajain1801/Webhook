package com.comviva.hceservice.tds;

import java.util.ArrayList;

/**
 * Created by amit.randhawa on 30-Aug-17.
 */

public interface TransactionHistoryListener {

    public void onSuccess(ArrayList<String> transactionInfo);
    public void onError(String error);
}
