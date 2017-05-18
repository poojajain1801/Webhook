package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * SsdData request
 * Created by amgoth.naik on 4/25/2017.
 */
@Getter
@Setter
public class ODAdata {
    private String appFileLocator;
    private String appProfile;
    private String caPubKeyIndex;
    private String iccPubKeyCert;
    private String tokenBinPubKeyCert;
    private String enciccPrivateKey;

    public ODAdata(String appFileLocator,String appProfile,String caPubKeyIndex,String iccPubKeyCert,String tokenBinPubKeyCert,String enciccPrivateKey) {
        this.appFileLocator=appFileLocator;
        this.appProfile=appProfile;
        this.caPubKeyIndex=caPubKeyIndex;
        this.iccPubKeyCert=iccPubKeyCert;
        this.tokenBinPubKeyCert=tokenBinPubKeyCert;
        this.enciccPrivateKey=enciccPrivateKey;
    }

    public ODAdata() {
    }
}
