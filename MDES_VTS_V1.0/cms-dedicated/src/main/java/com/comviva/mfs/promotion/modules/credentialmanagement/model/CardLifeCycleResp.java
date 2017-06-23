package com.comviva.mfs.promotion.modules.credentialmanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Delete Card Response
 * Created by tarkeshwar.v on 2/9/2017.
 */
@Getter
@Setter
public class CardLifeCycleResp {
    private String encryptedData;
    private String reasonCode;
    private String reasonDescription;

    public CardLifeCycleResp(String encryptedData, String reasonCode, String reasonDescription) {
        this.encryptedData = encryptedData;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    public CardLifeCycleResp() {
    }
}
