package com.comviva.hceservice.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterUserResponse {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("clientWalletAccountId")
    @Expose
    private String clientWalletAccountId;

    public String getClientWalletAccountId() {
        return clientWalletAccountId;
    }
    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }


}
