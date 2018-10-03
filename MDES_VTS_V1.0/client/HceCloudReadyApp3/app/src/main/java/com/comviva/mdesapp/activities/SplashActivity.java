package com.comviva.mdesapp.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.SdkException;
import com.comviva.mdesapp.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static com.visa.cbp.sdk.facade.util.ContextHelper.getApplicationContext;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private static int SPLASH_TIME_OUT = 3000;

    private void changeConfiguration(ComvivaSdk comvivaSdk) {
        Intent intent = new Intent(this, ConfigurationActivity.class);
        if (!comvivaSdk.isSdkInitialized()) {
            startActivity(intent);
            finish();
            return;
        }
        // Check that application is registered
        intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        relativeLayout = (RelativeLayout) findViewById(R.id.background);
        //relativeLayout.setBackground(getDrawable(R.drawable.splash));
        final ComvivaSdk comvivaSdk;
        try {
            comvivaSdk = ComvivaSdk.getInstance(getApplication());
            if(!comvivaSdk.isSdkInitialized()) {
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Configuration")
                        .setMessage("Do you want to change Configuration")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                changeConfiguration(comvivaSdk);
                            }
                        })
                        .setCancelable(false)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(SplashActivity.this, RegisterUserActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                finish();
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
        } catch (SdkException e) {
            new AlertDialog.Builder(SplashActivity.this)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


    }


    private void jumpToOtherActivity() {
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                // close this activity
                startActivity(intent);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }


}
