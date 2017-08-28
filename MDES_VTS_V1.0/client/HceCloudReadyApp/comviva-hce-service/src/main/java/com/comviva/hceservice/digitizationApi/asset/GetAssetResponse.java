package com.comviva.hceservice.digitizationApi.asset;

/**
 * Response of Get Asset API
 */
public class GetAssetResponse {
    private int responseCode;
    private String responseMessage;
    private MediaContent[] mediaContents;

    /**
     * @return  Response Code of the API
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Set response code.
     * @param responseCode  Response code
     */
    public void setResponseCode(int responseCode) {
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

    /**
     * Returns all Media Contents i.e. Assets as result.
     * @return  Media Contents
     */
    public MediaContent[] getMediaContents() {
        return mediaContents;
    }

    /**
     * Set media contents.
     * @param mediaContents Media Contents
     */
    public void setMediaContents(MediaContent[] mediaContents) {
        this.mediaContents = mediaContents;
    }
}
