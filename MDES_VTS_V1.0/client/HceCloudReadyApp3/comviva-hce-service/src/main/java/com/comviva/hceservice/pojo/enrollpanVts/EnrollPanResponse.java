package com.comviva.hceservice.pojo.enrollpanVts;

import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnrollPanResponse {

    @SerializedName("vPanEnrollmentID")
    @Expose
    private String vPanEnrollmentID;
    @SerializedName("paymentInstrument")
    @Expose
    private PaymentInstrument paymentInstrument;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("cardMetaData")
    @Expose
    private CardMetaData cardMetaData;


    public CardMetaData getCardMetaData() {

        return cardMetaData;
    }


    public String getvPanEnrollmentID() {

        return vPanEnrollmentID;
    }


    public PaymentInstrument getPaymentInstrument() {

        return paymentInstrument;
    }


    public String getResponseMessage() {

        return responseMessage;
    }


    public String getResponseCode() {

        return responseCode;
    }
}
