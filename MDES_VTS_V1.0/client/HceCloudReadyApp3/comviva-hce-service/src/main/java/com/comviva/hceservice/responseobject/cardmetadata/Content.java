package com.comviva.hceservice.responseobject.cardmetadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */
public class Content {

    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("mimeType")
    @Expose
    private String mimeType;


    public String getMimeType() {

        return mimeType;
    }


    public int getWidth() {

        return width;
    }


    public int getHeight() {

        return height;
    }


}
