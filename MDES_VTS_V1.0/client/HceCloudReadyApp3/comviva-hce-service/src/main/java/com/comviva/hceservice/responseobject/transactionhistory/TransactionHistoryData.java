package com.comviva.hceservice.responseobject.transactionhistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionHistoryData {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName(value = "transactions", alternate = {"txnHistory"})
    @Expose
    private List<TransactionDetails> transactionDetails;


    public List<TransactionDetails> getTransactionDetails() {

        return transactionDetails;
    }


    public String getResponseCode() {

        return responseCode;
    }


    public String getResponseMessage() {

        return responseMessage;
    }
}
