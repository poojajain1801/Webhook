package com.comviva.hceservice.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.provider.Settings;

/**
 * <p>Utility class contains APIs to check NFC feature's availability on a
 * android device and NFC feature is enabled or not.</p>
 */
public class NfcUtil {
    /**
     * Check that android device is having NFC feature oor not.
     * @param context Current Application Context
     * @return <code>true </code>Device is NFC capable <br>
     *         <code>false </code>Device has no is NFC feature <br>
     */
    public static boolean isNfcCapable(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // device has NFC functionality
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
            if (nfcAdapter != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether NFC feature is enabled or not on the device.
     * @param context Current Application Context
     * @return <code>true </code>NFC is enabled. <br>
     *         <code>false </code>NFC is disabled <br>
     *
     * Note - This method will return false if device has no NFC capability.
     */
    public static boolean isNfcEnabled(Context context) {
        // If device has no NFC feature then return false.
        if(!isNfcCapable(context)) {
            return false;
        }

        boolean isEnabled = false;
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

        // Particularly on Android 4.0.*, the isEnabled() method sometimes throws undocumented exceptions
        // when the NFC service had crashed before, so you might want to catch those exceptions. Moreover
        // the first call to isEnabled() after the NFC service had been stopped or crashed always returned false,
        // so it is advisable to always ignore the result of the first call of isEnabled().
        if (nfcAdapter != null) {
            try {
                isEnabled = nfcAdapter.isEnabled();
            } catch (Exception e) {
                try {
                    isEnabled = nfcAdapter.isEnabled();
                } catch (Exception ex) {
                }
            }
        }
        return isEnabled;
    }

    /**
     * Display NFC setting to enable or default application for Tap & Pay.
     * @param currentActivity Current Activity
     * @param nfcSetting NFC setting type
     */
    public static void showNfcSetting(Activity currentActivity, NfcSetting nfcSetting) {
        switch (nfcSetting) {
            case ENABLE_NFC:
                currentActivity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                break;

            case DEFAULT_TAP_N_PAY:
                if(isNfcEnabled(currentActivity.getApplicationContext())) {
                    currentActivity.startActivity(new Intent(Settings.ACTION_NFC_PAYMENT_SETTINGS));
                } else {
                    currentActivity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
                break;
        }
    }
}
