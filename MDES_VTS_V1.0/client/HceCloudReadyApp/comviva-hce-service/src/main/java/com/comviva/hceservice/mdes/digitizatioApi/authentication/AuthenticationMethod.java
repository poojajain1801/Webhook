package com.comviva.hceservice.mdes.digitizatioApi.authentication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Additional Authentication if required while digitization process.
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

    /**
     * Constructor.
     * @param id    Unique identifier assigned to this Authentication Method
     * @param type  Specifies the authentication method type
     * @param value Specifies the authentication method value (meaning varies depending on the authentication method type).
     */
    public AuthenticationMethod(Number id, AuthenticationType type, String value) {
        this.id = id;
        this.type = type;
        parseValue(value);
    }

    /**
     * Returns Value of Authentication Method
     * @return Value of Authentication Method
     */
    public String getValue() {
        return value;
    }

    /**
     * Set Value of Authentication Method
     * @param value Value of Authentication Method
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns Unique identifier.
     * @return  Unique identifier
     */
    public Number getId() {
        return id;
    }

    /**
     * Set Unique identifier.
     * @param id    Unique identifier
     */
    public void setId(Number id) {
        this.id = id;
    }

    /**
     * Returns Authentication Type.
     * @return Authentication Type
     */
    public AuthenticationType getType() {
        return type;
    }

    /**
     * Set Authentication Type.
     * @param type Authentication Type
     */
    public void setType(AuthenticationType type) {
        this.type = type;
    }

    /**
     * Returns Android Intent.
     * @return Android Intent
     */
    public AndroidIntent getAndroidIntent() {
        return androidIntent;
    }

    /**
     * Set Android Intent
     * @param androidIntent Android Intent
     */
    public void setAndroidIntent(AndroidIntent androidIntent) {
        this.androidIntent = androidIntent;
    }
}
