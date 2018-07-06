package com.comviva.mdesapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaHceService;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.util.ArrayUtil;

import java.util.List;

public class AndroidHceServiceApp extends HostApduService {
    public static final String TAG = "HostApduService";
    private static ApduLogListener apduLogListener;

    public static void setApduLogListener(ApduLogListener apduLogListener) {
        AndroidHceServiceApp.apduLogListener = apduLogListener;
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        String cmdApdu = ArrayUtil.getHexString(commandApdu);
        Log.d("Command APDU", cmdApdu);
        if(apduLogListener != null) {
            apduLogListener.onCommandApduReceived(cmdApdu);
        }

        ComvivaHceService comvivaHceService = ComvivaHceService.getInstance(getApplication());
        byte[] response = comvivaHceService.processCommandApdu(commandApdu, extras);
        if (response == null) {
            Log.d("Response APDU", "6985");
            return new byte[]{0x69, (byte) 0x85};
        }
        String respApdu = ArrayUtil.getHexString(response);
        Log.d("Response APDU", respApdu);
        if(apduLogListener != null) {
            apduLogListener.onResponseApdu(respApdu);
        }
        return response;
    }

    @Override
    public void onDeactivated(int reason) {
        String log = null;
        if (reason == DEACTIVATION_DESELECTED) {
            log = "onDeactivated DEACTIVATION_DESELECTED";
        } else if (reason == DEACTIVATION_LINK_LOSS) {
            log = "onDeactivated DEACTIVATION_LINK_LOSS";
        }

        Log.d(TAG, log);
        if(apduLogListener != null) {
            apduLogListener.onDeactivated(log + "\n----------------------------\n");
        }
        ComvivaHceService comvivaHceService = ComvivaHceService.getInstance(getApplication());
        comvivaHceService.onDeactivated(reason);
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
