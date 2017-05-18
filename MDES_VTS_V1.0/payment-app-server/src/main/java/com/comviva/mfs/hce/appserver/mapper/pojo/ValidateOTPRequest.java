package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * ValidateOTPRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ValidateOTPRequest {

    private String userId;
    private String activationCode;
    private String vProvisionedTokenID;
    private String otpValue;
    private String date;
    public ValidateOTPRequest(String userId, String activationCode,String vProvisionedTokenID,String otpValue,String date) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.vProvisionedTokenID=vProvisionedTokenID;
        this.otpValue=otpValue;
        this.date=date;
    }

    public ValidateOTPRequest() {
    }
}