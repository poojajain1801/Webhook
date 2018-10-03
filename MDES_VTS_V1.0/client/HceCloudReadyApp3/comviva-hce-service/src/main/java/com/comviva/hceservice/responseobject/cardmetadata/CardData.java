package com.comviva.hceservice.responseobject.cardmetadata;

import com.comviva.hceservice.responseobject.contentguid.AssetType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */
public class CardData {

    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("contentType")
    @Expose
    private AssetType contentType;
    @SerializedName("content")
    @Expose
    private List<Content> content;


    public String getGuid() {

        return guid;
    }


    public void setGuid(String guid) {

        this.guid = guid;
    }


    public AssetType getContentType() {

        return contentType;
    }


    public List<Content> getContent() {

        return content;
    }


    public void setContent(List<Content> content) {

        this.content = content;
    }
}
