package com.comviva.hceservice.pojo.registerdevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncryptedDevicePersoData {

    @SerializedName("walletAccountId")
    @Expose
    private String walletAccountId;
    @SerializedName("encExpo")
    @Expose
    private String encExpo;
    @SerializedName("encCert")
    @Expose
    private String encCert;
    @SerializedName("signCert")
    @Expose
    private String signCert;
    @SerializedName("signExpo")
    @Expose
    private String signExpo;
    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("encryptedDPM")
    @Expose
    private String encryptedDPM;
    @SerializedName("responseBody")
    @Expose
    private ResponseBody responseBody;


    public String getWalletAccountId() {

        return walletAccountId;
    }


    public String getEncExpo() {

        return encExpo;
    }


    public String getEncCert() {

        return encCert;
    }


    public String getSignCert() {

        return signCert;
    }


    public String getSignExpo() {

        return signExpo;
    }


    public String getDeviceId() {

        return deviceId;
    }


    public String getEncryptedDPM() {

        return encryptedDPM;
    }


    public ResponseBody getResponseBody() {

        return responseBody;
    }



}
