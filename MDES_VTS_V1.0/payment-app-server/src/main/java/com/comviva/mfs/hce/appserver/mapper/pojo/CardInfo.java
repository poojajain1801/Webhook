package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by tanmay.patel on 1/31/2017.
 */
@Getter
public class CardInfo {
    private String panUniqueReference;
    private String tokenUniqueReferenceForPanInfo;
    private String publicKeyFingerprint;
    private String encryptedKey;
    private String  OeapHashingAlgorithim;
    private String iv;
    private String encryptedData;

    public CardInfo(String panUniqueReference, String tokenUniqueReferenceForPanInfo, String publicKeyFingerprint, String encryptedKey, String oeapHashingAlgorithim, String iv, String encryptedData) {
        this.panUniqueReference = panUniqueReference;
        this.tokenUniqueReferenceForPanInfo = tokenUniqueReferenceForPanInfo;
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.encryptedKey = encryptedKey;
        OeapHashingAlgorithim = oeapHashingAlgorithim;
        this.iv = iv;
        this.encryptedData = encryptedData;
    }

    public CardInfo() {
    }
}
