package com.comviva.hceservice.pojo.checkcardeligibility;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplicableCardInfo {

    @SerializedName("isSecurityCodeApplicable")
    @Expose
    private boolean isSecurityCodeApplicable;

}
