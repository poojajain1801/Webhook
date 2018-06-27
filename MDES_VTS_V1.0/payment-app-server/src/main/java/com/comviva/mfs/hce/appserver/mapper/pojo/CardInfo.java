package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tanmay.patel on 1/31/2017.
 */
@Getter
@Setter
public class CardInfo {
    private String panUniqueReference;
    private String tokenUniqueReferenceForPanInfo;
    private String publicKeyFingerPrint;
    private String encryptedKey;
    private String hashingAlgorithm;
    private String iv;
    private String encryptedData;

    public CardInfo(String panUniqueReference, String tokenUniqueReferenceForPanInfo, String publicKeyFingerPrint, String encryptedKey, String hashingAlgorithm, String iv, String encryptedData) {
        this.panUniqueReference = panUniqueReference;
        this.tokenUniqueReferenceForPanInfo = tokenUniqueReferenceForPanInfo;
        this.publicKeyFingerPrint = publicKeyFingerPrint;
        this.encryptedKey = encryptedKey;
        this.hashingAlgorithm = hashingAlgorithm;
        this.iv = iv;
        this.encryptedData = encryptedData;
    }

    public CardInfo() {
    }
}
