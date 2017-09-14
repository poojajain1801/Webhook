package com.comviva.hceservice.util;

import android.provider.Settings;

import com.comviva.hceservice.common.ComvivaSdk;

/**
 * Miscellaneous utility methods.
 */
public class Miscellaneous {

    public static String padData(String data, int length) {
        final char PAD_CHAR = 'F';

        int paddingSize = length % data.length();
        StringBuilder paddedData = new StringBuilder(data);
        for(int i = 0; i < paddingSize; i++) {
            paddedData.append(PAD_CHAR);
        }
        return paddedData.toString();
    }

    public static String getUniqueClientDeviceId(String imei) {
        String androidId = Settings.Secure.getString(ComvivaSdk.getInstance().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId == null) {
            androidId = imei;
        }
        return Miscellaneous.padData(androidId, Constants.LEN_CLIENT_DEVICE_ID);
    }
}
