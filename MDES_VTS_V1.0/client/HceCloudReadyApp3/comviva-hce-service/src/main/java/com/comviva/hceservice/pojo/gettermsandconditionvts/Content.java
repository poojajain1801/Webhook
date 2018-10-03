package com.comviva.hceservice.pojo.gettermsandconditionvts;

import com.comviva.hceservice.responseobject.contentguid.AssetType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("mimeType")
    @Expose
    private AssetType mimeType;
    @SerializedName("encodedData")
    @Expose
    private String encodedData;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("width")
    @Expose
    private int width;


    public int getHeight() {

        return height;
    }


    public int getWidth() {

        return width;
    }


    public AssetType getMimeType() {

        return mimeType;
    }


    public String getEncodedData() {

        return encodedData;
    }
}
