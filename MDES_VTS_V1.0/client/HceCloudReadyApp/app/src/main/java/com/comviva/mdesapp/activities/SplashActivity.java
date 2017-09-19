package com.comviva.mdesapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.SdkException;
import com.comviva.mdesapp.R;

public class SplashActivity extends AppCompatActivity {

    private void changeConfiguration() {
        ComvivaSdk comvivaSdk;
        try {
            Intent intent = new Intent(this, ConfigurationActivity.class);
            comvivaSdk = ComvivaSdk.getInstance(getApplication());
            if (!comvivaSdk.isSdkInitialized()) {
                startActivity(intent);
                finish();
                return;
            }
            // Check that application is registered
            intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            this.finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Configuration")
                .setMessage("Do you want to change Configuration")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changeConfiguration();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SplashActivity.this, RegisterUserActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();



    }
}
