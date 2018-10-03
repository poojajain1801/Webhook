package com.comviva.hceservice.util;

import android.app.ActivityManager;
import android.content.Context;

import com.comviva.hceservice.common.SdkException;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Miscellaneous utility methods.
 */
public class Miscellaneous {





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
