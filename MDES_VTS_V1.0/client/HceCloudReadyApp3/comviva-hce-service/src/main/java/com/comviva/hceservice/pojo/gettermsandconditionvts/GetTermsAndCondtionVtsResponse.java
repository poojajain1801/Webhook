package com.comviva.hceservice.pojo.gettermsandconditionvts;

import com.comviva.hceservice.responseobject.contentguid.AssetType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTermsAndCondtionVtsResponse {

    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("altText")
    @Expose
    private String altText;
    @SerializedName("content")
    @Expose
    private List<Content> content;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;


    public String getAltText() {

        return altText;
    }


    public String getResponseCode() {

        return responseCode;
    }


    public String getResponseMessage() {

        return responseMessage;
    }


    public String getContentType() {

        return contentType;
    }


    public List<Content> getContent() {

        return content;
    }
}
