package com.comviva.hceservice.pojo.registerdevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobileKeys {

    public String getMacKey() {

        return macKey;
    }


    public String getDataEncryptionKey() {

        return dataEncryptionKey;
    }


    public String getTransportKey() {

        return transportKey;
    }


    @SerializedName("macKey")
    @Expose
    private String  macKey;

    @SerializedName("dataEncryptionKey")
    @Expose
    private String  dataEncryptionKey;

    @SerializedName("transportKey")
    @Expose
    private String  transportKey;

}
