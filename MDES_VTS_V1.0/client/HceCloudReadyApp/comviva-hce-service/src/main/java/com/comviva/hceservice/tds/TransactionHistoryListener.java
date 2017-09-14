package com.comviva.hceservice.tds;

import com.comviva.hceservice.common.CommonListener;

import java.util.ArrayList;

/**
 * Created by amit.randhawa on 30-Aug-17.
 */

public interface TransactionHistoryListener extends CommonListener {
    void onSuccess(ArrayList<String> transactionInfo);
}
