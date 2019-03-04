package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * SubmitIDandVStepupMethodRequest.
 * Created by Amgoth.madan on 5/02/2017.
 */

public class SubmitIDandVStepupMethodRequest {


    private String vProvisionedTokenID;
    private String stepUpRequestID;


    public SubmitIDandVStepupMethodRequest( String vProvisionedTokenID,String stepUpRequestID) {
        this.vProvisionedTokenID=vProvisionedTokenID;
        this.stepUpRequestID=stepUpRequestID;

    }

    public SubmitIDandVStepupMethodRequest() {
    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public String getStepUpRequestID() {
        return stepUpRequestID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public void setStepUpRequestID(String stepUpRequestID) {
        this.stepUpRequestID = stepUpRequestID;
    }
}