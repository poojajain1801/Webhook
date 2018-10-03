package com.comviva.hceservice.pojo.enrollpanVts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentInstrument {

    @SerializedName("last4")
    @Expose
    private String last4;

    @SerializedName("cvv2PrintedInd")
    @Expose
    private String cvv2PrintedInd;

    @SerializedName("expDatePrintedInd")
    @Expose
    private String expDatePrintedInd;


    @SerializedName("enabledServices")
    @Expose
    private EnabledServices enabledServices;


    @SerializedName("expirationDate")
    @Expose
    private ExpirationDate expirationDate;


    public String getLast4() {

        return last4;
    }


    public String getCvv2PrintedInd() {

        return cvv2PrintedInd;
    }


    public String getExpDatePrintedInd() {

        return expDatePrintedInd;
    }


    public EnabledServices getEnabledServices() {

        return enabledServices;
    }


    public ExpirationDate getExpirationDate() {

        return expirationDate;
    }







}
