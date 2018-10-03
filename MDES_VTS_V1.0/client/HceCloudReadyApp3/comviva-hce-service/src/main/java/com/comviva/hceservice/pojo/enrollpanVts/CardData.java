package com.comviva.hceservice.pojo.enrollpanVts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardData {

    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("content")
    @Expose
    private List<Content> content;

    public String getGuid() {

        return guid;
    }


    public String getContentType() {

        return contentType;
    }


    public List<Content>  getContent() {

        return content;
    }
}
