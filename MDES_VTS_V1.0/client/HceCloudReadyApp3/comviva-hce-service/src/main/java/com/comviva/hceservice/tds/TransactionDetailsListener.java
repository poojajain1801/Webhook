package com.comviva.hceservice.tds;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.transactionhistory.TransactionDetails;

import java.util.ArrayList;

/**
 * UI listener for fetching transaction details.
 */
public interface TransactionDetailsListener extends CommonListener {
    /**
     * Transaction Deails received successfully.
     * @param transactionDetails    List of transaction details.
     */
    void onSuccess(ArrayList<TransactionDetails> transactionDetails);
}
