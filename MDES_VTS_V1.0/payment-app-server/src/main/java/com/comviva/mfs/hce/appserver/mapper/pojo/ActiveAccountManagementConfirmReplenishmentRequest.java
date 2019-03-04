package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ActiveAccountManagementConfirmReplenishmentRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ActiveAccountManagementConfirmReplenishmentRequest {
    private String api;
    private String sc;
    private String vprovisionedTokenID;

    public ActiveAccountManagementConfirmReplenishmentRequest(String api, String sc, String vprovisionedTokenID) {
        this.api = api;
        this.sc = sc;
        this.vprovisionedTokenID = vprovisionedTokenID;
    }

    public ActiveAccountManagementConfirmReplenishmentRequest() {
    }
}