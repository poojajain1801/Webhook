package com.comviva.hceservice.util;

import android.provider.Settings;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.SdkException;

import java.util.Date;

/**
 * Miscellaneous utility methods.
 */
public class Miscellaneous {

    public static String padData(String data, int length) {

        /*if (data.length() >= length) {
            return data;
        }
        final char PAD_CHAR = 'F';

        int paddingSize = length - data.length();
        StringBuilder paddedData = new StringBuilder(data);
        for (int i = 0;
             i < paddingSize;
             i++) {
            paddedData.append(PAD_CHAR);
        }
        return paddedData.toString();*/
        final char PAD_CHAR = 'F';

        int paddingSize = length % data.length();
        StringBuilder paddedData = new StringBuilder(data);
        for(int i = 0; i < paddingSize; i++) {
            paddedData.append(PAD_CHAR);
        }
        return paddedData.toString();
    }

    public static String getUniqueClientDeviceId(String imei) throws SdkException {
       /* int lenRandNoLen = 4;
        byte[] random = ArrayUtil.getRandomNumber(lenRandNoLen);
        long currentTimeInMs = new Date().getTime();
        String clientDeviceId = ArrayUtil.getHexString(random) + String.format("%d", currentTimeInMs);
        return Miscellaneous.padData(clientDeviceId, Constants.LEN_CLIENT_DEVICE_ID);*/
        String androidId = Settings.Secure.getString(ComvivaSdk.getInstance(null).getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId == null) {
            androidId = imei;
        }
        return Miscellaneous.padData(androidId, Constants.LEN_CLIENT_DEVICE_ID);
    }
}
