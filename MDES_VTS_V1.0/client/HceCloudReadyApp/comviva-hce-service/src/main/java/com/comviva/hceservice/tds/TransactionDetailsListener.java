package com.comviva.hceservice.tds;

import java.util.ArrayList;

/**
 * UI listener for fetching transaction details.
 */
public interface TransactionDetailsListener {
    /**
     * Request for Transaction Details started
     */
    void onStarted();

    /**
     * Error Occurred.
     * @param message   Error Message
     */
    void onError(String message);

    /**
     * Transaction Deails received successfully.
     * @param transactionDetails    List of transaction details.
     */
    void onSuccess(ArrayList<TransactionDetails> transactionDetails);
}
