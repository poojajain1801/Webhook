package com.comviva.mdesapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.comviva.hceservice.common.ComvivaHce;

import com.comviva.mdesapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, RegisterUserActivity.class);
        if (!ComvivaHce.getInstance(getApplication()).isSdkInitialized()) {
            startActivity(intent);
            finish();
            return;
        }
        // Check that application is registered
        intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }
}