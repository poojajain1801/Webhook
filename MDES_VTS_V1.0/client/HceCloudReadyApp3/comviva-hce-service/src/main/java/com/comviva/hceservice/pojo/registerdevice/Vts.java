package com.comviva.hceservice.pojo.registerdevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vts {

    @SerializedName("encDevicePersoData")
    @Expose
    private EncryptedDevicePersoData encDevicePersoData;

    @SerializedName("statusMessage")
    @Expose
    private String statusMessage;
    @SerializedName("statusCode")
    @Expose
    private String statusCode;

    public EncryptedDevicePersoData getEncDevicePersoData() {

        return encDevicePersoData;
    }


    public String getStatusMessage() {

        return statusMessage;
    }


    public String getStatusCode() {

        return statusCode;
    }


}
