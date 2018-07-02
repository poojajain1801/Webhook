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
    private String vProvisionedTokenID;
    private String stepUpRequestID;

    public SubmitIDandVStepupMethodRequest(String vProvisionedTokenID, String stepUpRequestID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
        this.stepUpRequestID = stepUpRequestID;
    }

    public SubmitIDandVStepupMethodRequest() {
    }
}