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
    private String api;
    private String provisioningStatus;
    private String vprovisionedTokenID;
    private String failureReason;

    public ConfirmProvisioningRequest(String api, String provisioningStatus, String vprovisionedTokenId, String failureReason) {
        this.api = api;
        this.provisioningStatus = provisioningStatus;
        this.vprovisionedTokenID = vprovisionedTokenId;
        this.failureReason = failureReason;
    }

    public ConfirmProvisioningRequest() {

    }


}