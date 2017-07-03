package com.comviva.hceservice.mdes.digitizatioApi.authentication;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by tarkeshwar.v on 6/20/2017.
 */
public class AndroidIntent {
    /** The name of the action to be performed. This is a fully qualified name including the
     * package name in order to create an explicit intent */
    private String action;

    /** The package name of the issuer's mobile app. This identifies the app that the intent will resolve to.
     * If the app is not installed on the user's device, this package name can be used to open a link to the
     * appropriate Android app store for the user to download and install the app. */
    private String packageName;

    private MobileAppActivationParameters mobileAppActivationParameters;

    /**
     * Parses Extra Text Value in MobileAppActivationParameters
     * @param extraTextValue Extra Text Value
     */
    private void parseExtraTextValue(final String extraTextValue) {
        mobileAppActivationParameters = new MobileAppActivationParameters();
        byte[] data = Base64.decode(extraTextValue, Base64.DEFAULT);
        try {
            JSONObject jsMobileAppActivationParameters = new JSONObject(new String(data, StandardCharsets.UTF_8));
            mobileAppActivationParameters.setPaymentAppProviderId(jsMobileAppActivationParameters.getString("paymentAppProviderId"));
            mobileAppActivationParameters.setPaymentAppInstanceId(jsMobileAppActivationParameters.getString("paymentAppInstanceId"));
            mobileAppActivationParameters.setTokenUniqueReference(jsMobileAppActivationParameters.getString("tokenUniqueReference"));
            mobileAppActivationParameters.setAccountPanSuffix(jsMobileAppActivationParameters.getString("accountPanSuffix"));
            mobileAppActivationParameters.setAccountExpiry(jsMobileAppActivationParameters.getString("accountPanSuffix"));
        } catch (JSONException e) {
        }
    }

    public AndroidIntent(final String action, final String packageName, final String extraTextValue) {
        this.action = action;
        this.packageName = packageName;
        parseExtraTextValue(extraTextValue);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public MobileAppActivationParameters getMobileAppActivationParameters() {
        return mobileAppActivationParameters;
    }

    public void setMobileAppActivationParameters(MobileAppActivationParameters mobileAppActivationParameters) {
        this.mobileAppActivationParameters = mobileAppActivationParameters;
    }

}
