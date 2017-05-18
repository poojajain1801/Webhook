package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * SubmitIDandVStepupMethodRequest.
 * Created by Amgoth.madan on 5/02/2017.
 */
@Getter
@Setter
public class SubmitIDandVStepupMethodRequest {

    private String userId;
    private String activationCode;
    private String vProvisionedTokenID;
    private String stepUpRequestID;
    private String date;

    public SubmitIDandVStepupMethodRequest(String userId, String activationCode, String vProvisionedTokenID,String stepUpRequestID,String date) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.vProvisionedTokenID=vProvisionedTokenID;
        this.stepUpRequestID=stepUpRequestID;
        this.date=date;
    }

    public SubmitIDandVStepupMethodRequest() {
    }
}