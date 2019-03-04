package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * DeleteTokenRequest.
 * Created by Amgoth.madan on 5/10/2017.
 */
@Getter
@Setter
public class LifeCycleManagementVisaRequest {
    private String vprovisionedTokenID;
    private String reasonCode;
    private String reasonDesc;
    private String operation;

    public LifeCycleManagementVisaRequest(String vprovisionedTokenID, String reasonCode, String reasonDesc, String operation) {
        this.vprovisionedTokenID = vprovisionedTokenID;
        this.reasonCode = reasonCode;
        this.reasonDesc = reasonDesc;
        this.operation = operation;
    }

    public LifeCycleManagementVisaRequest() {
    }
}