package com.comviva.hceservice.pojo.registerdevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mdes {

    @SerializedName("mobileKeysetId")
    @Expose
    private String mobileKeysetId;
    @SerializedName("remoteManagementUrl")
    @Expose
    private String remoteManagementUrl;

    @SerializedName("mobileKeys")
    @Expose
    private MobileKeys mobileKeys;

    @SerializedName("responseHost")
    @Expose
    private String responseHost;

    @SerializedName("responseId")
    @Expose
    private String responseId;

    public String getMobileKeysetId() {

        return mobileKeysetId;
    }


    public String getRemoteManagementUrl() {

        return remoteManagementUrl;
    }


    public MobileKeys getMobileKeys() {

        return mobileKeys;
    }


    public String getResponseHost() {

        return responseHost;
    }


    public String getResponseId() {

        return responseId;
    }

}
