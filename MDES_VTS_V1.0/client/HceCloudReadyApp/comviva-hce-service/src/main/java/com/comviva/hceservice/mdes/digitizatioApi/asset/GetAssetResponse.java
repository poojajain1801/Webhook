package com.comviva.hceservice.mdes.digitizatioApi.asset;

/**
 * Response of Get A
 * Created by tarkeshwar.v on 5/25/2017.
 */
public class GetAssetResponse {
    private int responseCode;
    private String responseMessage;
    private MediaContent[] mediaContents;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public MediaContent[] getMediaContents() {
        return mediaContents;
    }

    public void setMediaContents(MediaContent[] mediaContents) {
        this.mediaContents = mediaContents;
    }
}
