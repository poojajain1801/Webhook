package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ActiveAccountManagementReplenishRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ActiveAccountManagementReplenishRequest {

    private String userId;
    private String activationCode;
    private String mac;
    private String api;
    private String sc;
    private String tvl;
    private String encryptionMetaData;


    public ActiveAccountManagementReplenishRequest(String userId, String activationCode, String mac, String api, String sc, String tvl,String encryptionMetaData) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.mac=mac;
        this.api=api;
        this.sc=sc;
        this.tvl=tvl;
        this.encryptionMetaData=encryptionMetaData;
    }

    public ActiveAccountManagementReplenishRequest() {
    }
}