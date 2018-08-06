package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 06-08-2018.
 */
@Getter
@Setter
public class EncryptedPayload {
    private String publicKeyFingerprint ;
    private String encryptedKey;
    private String encryptedData;

    public EncryptedPayload(String publicKeyFingerprint, String encryptedKey, String encryptedData) {
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.encryptedKey = encryptedKey;
        this.encryptedData = encryptedData;
    }

    public EncryptedPayload() {
    }
}
