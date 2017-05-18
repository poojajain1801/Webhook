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
    private String userId;
    private String activationCode;
    private TokennInfo tokenInfo;

    public ActiveAccountManagementConfirmReplenishmentRequest(String userId, String activationCode, TokennInfo tokenInfo) {
        this.userId=userId;
        this.activationCode=activationCode;
        this.tokenInfo=tokenInfo;
    }

    public ActiveAccountManagementConfirmReplenishmentRequest() {
    }
}