package com.comviva.hceservice.mdes.digitizatioApi.authentication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by tarkeshwar.v on 6/20/2017.
 */
public class AuthenticationMethod implements Serializable {
    private Number id;
    private AuthenticationType type;
    private String value;
    private AndroidIntent androidIntent;

    private void parseValue(final String value) {
        switch (type) {
            case CARDHOLDER_TO_USE_ISSUER_MOBILE_APP:
                try {
                    JSONObject jsValue = new JSONObject(value);
                    JSONObject jsAndroidIntent;

                    if(jsValue.has("activateWithIssuerMobileAppAndroidIntent")) {
                        jsAndroidIntent = new JSONObject(jsValue.getString("activateWithIssuerMobileAppAndroidIntent"));

                        // Prepare androidIntent
                        androidIntent = new AndroidIntent(jsAndroidIntent.getString("action"),
                                jsAndroidIntent.getString("packageName"),
                                jsAndroidIntent.getString("extraTextValue"));
                    }
                } catch (JSONException e) {
                }
                break;

            default:
            // Do nothing as value is simply a plain string
                this.value = value;
        }
    }

    public AuthenticationMethod(Number id, AuthenticationType type, String value) {
        this.id = id;
        this.type = type;
        parseValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public AuthenticationType getType() {
        return type;
    }

    public void setType(AuthenticationType type) {
        this.type = type;
    }

    public AndroidIntent getAndroidIntent() {
        return androidIntent;
    }

    public void setAndroidIntent(AndroidIntent androidIntent) {
        this.androidIntent = androidIntent;
    }
}
