package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * ValidateAuthenticationCodeRequest.
 * Created by Amgoth.madan on 5/5/2017.
 */
@Getter
@Setter
public class ValidateAuthenticationCodeRequest {

    private String userId;
    private String activationCode;
    private String vProvisionedTokenID;
    private String issuerAuthCode;
    private Date date;


    public ValidateAuthenticationCodeRequest(String userId, String activationCode, String vProvisionedTokenID, String issuerAuthCode,Date date) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.vProvisionedTokenID=vProvisionedTokenID;
        this.issuerAuthCode=issuerAuthCode;
        this.date=date;
    }

    public ValidateAuthenticationCodeRequest() {
    }
}