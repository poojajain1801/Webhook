package com.comviva.mfs.promotion.modules.mobilepaymentapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Provision Response from CMS to MP-SDK
 * Created by tarkeshwar.v on 2/2/2017.
 */
@Getter
@Setter
public class ProvisionResponseMpa {
    private String encryptedData;
    private String reasonCode;
    private String reasonDescription;

    public ProvisionResponseMpa(String encryptedData, String reasonCode, String reasonDescription) {
        this.encryptedData = encryptedData;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    public ProvisionResponseMpa() {
    }
}
