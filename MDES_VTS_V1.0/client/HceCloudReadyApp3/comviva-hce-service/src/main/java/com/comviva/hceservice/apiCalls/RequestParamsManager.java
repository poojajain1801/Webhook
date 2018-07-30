package com.comviva.hceservice.apiCalls;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestParamsManager {

    public static JSONObject getVerifyOtpParams(String provisionID, String otpValue) throws JSONException {


        JSONObject verifyOTPObject = new JSONObject();
        verifyOTPObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), provisionID);
        verifyOTPObject.put(Tags.OTP_VALUE.getTag(), otpValue);
        return verifyOTPObject;
    }


    public static JSONObject getStepUpParams(String provisionID) throws JSONException {

        JSONObject stepUpObject = new JSONObject();
        stepUpObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), provisionID);
        return stepUpObject;


    }
}
