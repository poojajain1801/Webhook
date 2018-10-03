package com.comviva.hceservice.responseobject.authenticationmethods;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenInfo {

    @SerializedName("accountPanSuffix")
    @Expose
    private String accountPanSuffix;
    @SerializedName("tokenPanSuffix")
    @Expose
    private String tokenPanSuffix;
    @SerializedName("tokenExpiry")
    @Expose
    private String tokenExpiry;
    @SerializedName("dsrpCapable")
    @Expose
    private String dsrpCapable;


    public String getAccountPanSuffix() {

        return accountPanSuffix;
    }


    public void setAccountPanSuffix(String accountPanSuffix) {

        this.accountPanSuffix = accountPanSuffix;
    }


    public String getTokenPanSuffix() {

        return tokenPanSuffix;
    }


    public void setTokenPanSuffix(String tokenPanSuffix) {

        this.tokenPanSuffix = tokenPanSuffix;
    }


    public String getTokenExpiry() {

        return tokenExpiry;
    }


    public void setTokenExpiry(String tokenExpiry) {

        this.tokenExpiry = tokenExpiry;
    }


    public String getDsrpCapable() {

        return dsrpCapable;
    }


    public void setDsrpCapable(String dsrpCapable) {

        this.dsrpCapable = dsrpCapable;
    }
}
