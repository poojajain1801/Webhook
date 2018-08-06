package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 03-08-2018.
 */
@Getter
@Setter
public class TokenCredential {
    private String encryptedData ;
    private String ccmNonce ;
    private String ccmKeyId ;
    private String ccmMac ;

    public TokenCredential(String encryptedData, String ccmNonce, String ccmKeyId, String ccmMac) {
        this.encryptedData = encryptedData;
        this.ccmNonce = ccmNonce;
        this.ccmKeyId = ccmKeyId;
        this.ccmMac = ccmMac;
    }
}

