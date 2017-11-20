package com.comviva.mdesapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.comviva.mdesapp.activities.HomeActivity;

public class UiUtil {
    public static void showDialog(final Activity context, String title, String message) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
    }

    /**
     * Checks for permission is granted or not.
     *
     * @param permission Permission to check
     * @return <code>PackageManager.PERMISSION_GRANTED</code> Permission is granted <br/>
     * <code>PackageManager.PERMISSION_DENIED</code> Permission denied <br/>
     */
    public static boolean checkPermission(Activity context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks for permission is granted or not.
     *
     * @param permission Permission to check
     */
    public static void getPermission(Activity context, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }
    }
}
