package com.comviva.hceservice.pojo.registerdevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterDeviceResponse {

    @SerializedName("mdesFinalCode")
    @Expose
    private String mdesFinalCode;
    @SerializedName("visaFinalMessage")
    @Expose
    private String visaFinalMessage;
    @SerializedName("vts")
    @Expose
    private Vts vts;
    @SerializedName("hvtThreshold")
    @Expose
    private double hvtThreshold;
    @SerializedName("mdes")
    @Expose
    private Mdes mdes;
    @SerializedName("mdesFinalMessage")
    @Expose
    private String mdesFinalMessage;
    @SerializedName("visaFinalCode")
    @Expose
    private String visaFinalCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("isHvtSupported")
    @Expose
    private Boolean isHvtSupported;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;



    public String getMdesFinalMessage() {

        return mdesFinalMessage;
    }


    public String getVisaFinalCode() {

        return visaFinalCode;
    }


    public String getMessage() {

        return message;
    }


    public Boolean getIsHvtSupported() {

        return isHvtSupported;
    }


    public String getResponseCode() {

        return responseCode;
    }

    public double getHvtThreshold() {

        return hvtThreshold;
    }


    public Mdes getMdes() {

        return mdes;
    }


    public Vts getVts() {

        return vts;
    }


    public String getMdesFinalCode() {

        return mdesFinalCode;
    }


    public String getVisaFinalMessage() {

        return visaFinalMessage;
    }
}
