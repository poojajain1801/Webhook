package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ConfirmProvisioningRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ConfirmProvisioningRequest {

    private String userId;
    private String activationCode;
    private String api;
    private String provisioningStatus;
    private String failureReason;
    private String reperso;


    public ConfirmProvisioningRequest(String userId, String activationCode, String api, String provisioningStatus, String failureReason, String reperso) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.api=api;
        this.provisioningStatus=provisioningStatus;
        this.failureReason=failureReason;
        this.reperso=reperso;
    }

    public ConfirmProvisioningRequest() {
    }
}