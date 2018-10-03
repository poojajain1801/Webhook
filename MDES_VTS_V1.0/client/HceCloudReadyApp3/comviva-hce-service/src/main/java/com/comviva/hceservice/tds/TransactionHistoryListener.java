package com.comviva.hceservice.tds;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.transactionhistory.TransactionDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amit.randhawa on 30-Aug-17.
 */
public interface TransactionHistoryListener extends CommonListener {
    void onSuccess(List<TransactionDetails> transactionHistoryDetailsLists);
}
