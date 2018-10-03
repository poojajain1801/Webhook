package com.comviva.hceservice.responseobject.cardmetadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */
public class UnsupportedCardVerificationTypes {

    @SerializedName("aid")
    @Expose
    private String aid;
    @SerializedName("priority")
    @Expose
    private String priority;


    public String getAid() {

        return aid;
    }


    public String getPriority() {

        return priority;
    }
}
