package com.comviva.hceservice.pojo.enrollpanVts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnabledServices {

    @SerializedName("merchantPresentedQR")
    @Expose
    private String merchantPresentedQR;

    public String getMerchantPresentedQR() {

        return merchantPresentedQR;
    }

}
