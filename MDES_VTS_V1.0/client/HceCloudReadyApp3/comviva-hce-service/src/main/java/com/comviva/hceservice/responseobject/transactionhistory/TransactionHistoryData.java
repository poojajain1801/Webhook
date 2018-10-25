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
    @SerializedName("transactions")
    @Expose
    private List<TransactionDetails> transactionDetails;
    @SerializedName("transactionDetails")
    @Expose
    private List<TxnHistoryVisaData> txnHistoryVisaData;


    public List<TxnHistoryVisaData> getTxnHistoryVisaData() {

        return txnHistoryVisaData;
    }


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
