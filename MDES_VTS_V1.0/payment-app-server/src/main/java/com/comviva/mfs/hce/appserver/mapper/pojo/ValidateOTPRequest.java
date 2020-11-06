package com.comviva.mfs.hce.appserver.mapper.pojo;



/**
 * ValidateOTPRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */

public class ValidateOTPRequest {

    private String vProvisionedTokenID;
    private String otpValue;


    public ValidateOTPRequest(String vProvisionedTokenID, String otpValue, String date) {
        this.vProvisionedTokenID = vProvisionedTokenID;
        this.otpValue = otpValue;
    }

    public ValidateOTPRequest() {
    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public String getOtpValue() {
        return otpValue;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }
}