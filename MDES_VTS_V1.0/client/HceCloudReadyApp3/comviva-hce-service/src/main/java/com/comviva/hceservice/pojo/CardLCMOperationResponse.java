package com.comviva.hceservice.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardLCMOperationResponse {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;


    public String getResponseCode() {

        return responseCode;
    }


    public String getResponseMessage() {

        return responseMessage;
    }
}
