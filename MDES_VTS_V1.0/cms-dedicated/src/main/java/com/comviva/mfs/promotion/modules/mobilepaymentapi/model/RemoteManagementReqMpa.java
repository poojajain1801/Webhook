package com.comviva.mfs.promotion.modules.mobilepaymentapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Provision request received by MP-SDK.
 * Created by tarkeshwar.v on 2/2/2017.
 */
@Getter
@Setter
public class RemoteManagementReqMpa {
    private String mobileKeysetId;
    private String authenticationCode;
    private String encryptedData;

    public RemoteManagementReqMpa(String mobileKeysetId, String authenticationCode, String encryptedData) {
        this.mobileKeysetId = mobileKeysetId;
        this.authenticationCode = authenticationCode;
        this.encryptedData = encryptedData;
    }

    public RemoteManagementReqMpa() {
    }
}
