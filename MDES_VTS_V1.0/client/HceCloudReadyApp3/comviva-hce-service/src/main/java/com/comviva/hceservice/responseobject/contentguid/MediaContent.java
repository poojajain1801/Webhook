package com.comviva.hceservice.responseobject.contentguid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Media Content object.
 */
public class MediaContent implements Serializable {
    @SerializedName("type")
    @Expose
    private AssetType assetType;

    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("width")
    @Expose
    private int width;


    public void setAssetType(AssetType assetType) {

        this.assetType = assetType;
    }


    public void setData(String data) {

        this.data = data;
    }


    public void setHeight(int height) {

        this.height = height;
    }


    public void setWidth(int width) {

        this.width = width;
    }




    public AssetType getAssetType() {

        return assetType;
    }


    public String getData() {

        return data;
    }


    public int getHeight() {

        return height;
    }


    public int getWidth() {

        return width;
    }
}