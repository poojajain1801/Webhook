package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.List;

/**
 * ActiveAccountManagementReplenishRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ActiveAccountManagementReplenishRequest {

    private String mac;
    private String api;
    private String sc;
    private List tvl;
    private String vprovisionedTokenID;

    public ActiveAccountManagementReplenishRequest(String mac, String api, String sc, List tvl, String vprovisionedTokenID) {
        this.mac = mac;
        this.api = api;
        this.sc = sc;
        this.tvl = tvl;
        this.vprovisionedTokenID = vprovisionedTokenID;
    }

    public ActiveAccountManagementReplenishRequest() {
    }
}