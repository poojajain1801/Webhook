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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, RegisterUserActivity.class);

        ComvivaSdk comvivaSdk = null;
        try {
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
}
