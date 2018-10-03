package com.comviva.hceservice.pojo.enrollpanVts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpirationDate {

    @SerializedName("month")
    @Expose
    private String month;

    @SerializedName("year")
    @Expose
    private String year;

    public String getMonth() {

        return month;
    }


    public String getYear() {

        return year;
    }




}
