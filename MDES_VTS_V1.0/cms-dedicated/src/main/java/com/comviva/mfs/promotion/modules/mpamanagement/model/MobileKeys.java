package com.comviva.mfs.promotion.modules.mpamanagement.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileKeys {
    private String transportKey;
    private String macKey;
    private String dataEncryptionKey;

    public MobileKeys(String transportKey, String macKey, String dataEncryptionKey) {
        this.transportKey = transportKey;
        this.macKey = macKey;
        this.dataEncryptionKey = dataEncryptionKey;
    }

}
