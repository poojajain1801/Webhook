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
    private String oaepHashingAlgorithm;
    private String iv;

    public EncryptedPayload(String publicKeyFingerprint, String encryptedKey, String encryptedData, String oaepHashingAlgorithm, String iv) {
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.encryptedKey = encryptedKey;
        this.encryptedData = encryptedData;
        this.oaepHashingAlgorithm = oaepHashingAlgorithm;
        this.iv = iv;
    }

    public EncryptedPayload() {


    }
}

