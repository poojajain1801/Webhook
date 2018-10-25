package com.comviva.hceservice.responseobject.transactionhistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TxnHistoryVisaData implements Serializable {

    @SerializedName("txnHistory")
    @Expose
    private String transactionDetails;


    public String getTransactionDetails() {

        return transactionDetails;
    }
}
