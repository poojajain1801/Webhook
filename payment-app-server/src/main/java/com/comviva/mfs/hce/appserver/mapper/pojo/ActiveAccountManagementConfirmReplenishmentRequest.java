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
    private String vProvisionedTokenID;

    public ActiveAccountManagementConfirmReplenishmentRequest(String api, String sc, String vProvisionedTokenID) {
        this.api = api;
        this.sc = sc;
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public ActiveAccountManagementConfirmReplenishmentRequest() {
    }
}