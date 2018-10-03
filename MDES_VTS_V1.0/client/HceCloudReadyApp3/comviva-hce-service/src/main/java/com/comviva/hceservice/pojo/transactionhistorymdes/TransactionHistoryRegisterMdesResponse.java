package com.comviva.hceservice.pojo.transactionhistorymdes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionHistoryRegisterMdesResponse {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("registrationStatus")
    @Expose
    private String registrationStatus;


    public String getRegistrationStatus() {

        return registrationStatus;
    }


    public String getResponseCode() {

        return responseCode;
    }


    public String getResponseMessage() {

        return responseMessage;
    }
}
