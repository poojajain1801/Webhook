package com.comviva.hceservice.digitizationApi.authentication;

/**
 * Created by tarkeshwar.v on 6/20/2017.
 */
public class IssuerMobileApp {
    private AndroidIntent openIssuerMobileAppAndroidIntent;
    private AndroidIntent activateWithIssuerMobileAppAndroidIntent;

    public AndroidIntent getOpenIssuerMobileAppAndroidIntent() {
        return openIssuerMobileAppAndroidIntent;
    }

    public void setOpenIssuerMobileAppAndroidIntent(AndroidIntent openIssuerMobileAppAndroidIntent) {
        this.openIssuerMobileAppAndroidIntent = openIssuerMobileAppAndroidIntent;
    }

    public AndroidIntent getActivateWithIssuerMobileAppAndroidIntent() {
        return activateWithIssuerMobileAppAndroidIntent;
    }

    public void setActivateWithIssuerMobileAppAndroidIntent(AndroidIntent activateWithIssuerMobileAppAndroidIntent) {
        this.activateWithIssuerMobileAppAndroidIntent = activateWithIssuerMobileAppAndroidIntent;
    }

}
