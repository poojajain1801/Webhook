package com.comviva.hceservice.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.visa.cbp.external.common.StepUpRequest;

import java.util.ArrayList;

public class StepUpResponse {
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("stepUpRequest")
    @Expose
    private ArrayList<StepUpRequest> stepUpRequest;

    public ArrayList<StepUpRequest> getStepUpRequest() {
        return stepUpRequest;
    }


    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}

