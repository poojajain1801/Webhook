package com.comviva.hceservice.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by tarkeshwar.v on 3/9/2017.
 */
public class Permission {
    /**
     * Checks for permission is granted or not.
     *
     * @param permission Permission to check
     * @param currentActivity Current Activity
     * @return <code>PackageManager.PERMISSION_GRANTED</code> Permission is granted <br/>
     * <code>PackageManager.PERMISSION_DENIED</code> Permission denied <br/>
     */
    public static int checkPermission(final String permission, Activity currentActivity) {
        return ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.WRITE_CALENDAR);
    }

    /**
     * Checks for permission is granted or not.
     *
     * @param permission Permission to check
     */
    public static void getPermission(String permission, final int reqCode, Activity currentActivity) {
        if (ContextCompat.checkSelfPermission(currentActivity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(currentActivity, new String[]{permission}, reqCode);
        }
    }
}
