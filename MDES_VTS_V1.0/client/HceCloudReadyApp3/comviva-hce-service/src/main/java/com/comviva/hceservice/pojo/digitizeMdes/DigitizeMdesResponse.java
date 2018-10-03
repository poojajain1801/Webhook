package com.comviva.hceservice.pojo.digitizeMdes;

import com.comviva.hceservice.responseobject.authenticationmethods.AuthenticationMethod;
import com.comviva.hceservice.responseobject.authenticationmethods.TokenInfo;
import com.comviva.hceservice.responseobject.cardmetadata.ProductConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DigitizeMdesResponse {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("decision")
    @Expose
    private String decision;
    @SerializedName("tdsRegistrationUrl")
    @Expose
    private String tdsRegistrationUrl;
    @SerializedName("authenticationMethods")
    @Expose
    private List<AuthenticationMethod> authenticationMethodList;
    @SerializedName("productConfig")
    @Expose
    private ProductConfig productConfig;
    @SerializedName("panUniqueReference")
    @Expose
    private String panUniqueReference;
    @SerializedName("tokenUniqueReference")
    @Expose
    private String tokenUniqueReference;
    @SerializedName("tokenInfo")
    @Expose
    private TokenInfo tokenInfo;


    public String getDecision() {

        return decision;
    }


    public ProductConfig getProductConfig() {

        return productConfig;
    }


    public String getTdsRegistrationUrl() {

        return tdsRegistrationUrl;
    }


    public List<AuthenticationMethod> getAuthenticationMethodList() {

        return authenticationMethodList;
    }


    public String getPanUniqueReference() {

        return panUniqueReference;
    }


    public String getTokenUniqueReference() {

        return tokenUniqueReference;
    }


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
