package com.comviva.mfs.promotion.modules.credentialmanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Delete Card Request.
 * Created by tarkeshwar.v on 2/9/2017.
 */
@Getter
@Setter
public class CardLifeCycleReq {
    private String mobileKeysetId;
    private String authenticationCode;
    private String encryptedData;

    public CardLifeCycleReq(String mobileKeysetId, String authenticationCode, String encryptedData) {
        this.mobileKeysetId = mobileKeysetId;
        this.authenticationCode = authenticationCode;
        this.encryptedData = encryptedData;
    }

    public CardLifeCycleReq() {
    }

}
