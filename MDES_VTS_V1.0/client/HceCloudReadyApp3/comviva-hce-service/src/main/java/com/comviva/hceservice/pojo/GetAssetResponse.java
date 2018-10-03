package com.comviva.hceservice.pojo;

import com.comviva.hceservice.responseobject.contentguid.MediaContent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Response of Get Asset API
 */
public class GetAssetResponse {
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;

    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("mediaContents")
    @Expose
    private MediaContent[] mediaContents;

    /**
     * @return  Response Code of the API
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Set response code.
     * @param responseCode  Response code
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return  Response message
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Set Response Message
     * @param responseMessage   Response Message
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


    public String getContentType() {

        return contentType;
    }
    /**
     * Returns all Media Contents i.e. Assets as result.
     * @return  Media Contents
     */
    public MediaContent[] getMediaContents() {
        return (mediaContents);
    }

    /**
     * Set media contents.
     * @param mediaContents Media Contents
     */
    public void setMediaContents(MediaContent[] mediaContents) {
        this.mediaContents = (mediaContents);
    }
}
