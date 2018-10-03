package com.comviva.hceservice.pojo.tokenstatusupdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.visa.cbp.external.common.TokenInfo;

public class TokenUpdateResponse {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("tokenInfo")
    @Expose
    private TokenInfo tokenInfo;


    public TokenInfo getTokenInfo() {

        return tokenInfo;
    }


    public String getResponseCode() {

        return responseCode;
    }


    public String getResponseMessage() {

        return responseMessage;
    }
}
