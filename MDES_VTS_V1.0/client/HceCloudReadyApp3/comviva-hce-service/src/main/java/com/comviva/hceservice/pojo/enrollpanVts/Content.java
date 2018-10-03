package com.comviva.hceservice.pojo.enrollpanVts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("width")
    @Expose
    private int width;


    @SerializedName("mimeType")
    @Expose
    private String mimeType;

    @SerializedName("height")
    @Expose
    private int height;


    public int getWidth() {

        return width;
    }


    public String getMimeType() {

        return mimeType;
    }


    public int getHeight() {

        return height;
    }




}
