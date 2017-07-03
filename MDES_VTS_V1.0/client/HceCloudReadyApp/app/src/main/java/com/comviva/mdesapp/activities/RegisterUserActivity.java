package com.comviva.mdesapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.register.RegisterUserListener;
import com.comviva.hceservice.register.RegisterUserResponse;
import com.comviva.hceservice.register.Registration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import com.comviva.mdesapp.R;
import com.comviva.mdesapp.UiUtil;

public class RegisterUserActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        final EditText edUserId = (EditText) findViewById(R.id.editUserIdRegUser);
        final Button btnRegUser = (Button) findViewById(R.id.btnRegUser);

        btnRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComvivaHce.getInstance(null).getInitializationData();

                if(!UiUtil.checkPermission(RegisterUserActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                    UiUtil.getPermission(RegisterUserActivity.this, Manifest.permission.READ_PHONE_STATE, 0);
                    return;
                }
                userId = edUserId.getText().toString();
                Registration registration = new Registration();
                String imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

                registration.registerUser(userId, imei, Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName(), Build.MODEL, registerUserListener);
            }
        });
    }

    final RegisterUserListener registerUserListener = new RegisterUserListener() {
        private RegisterUserResponse registerUserResponse;

        @Override
        public void onRegistrationStarted() {
            progressDialog = new ProgressDialog(RegisterUserActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public void onRegistrationCompeted() {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Intent actUser = new Intent(RegisterUserActivity.this, ActivateUserActivity.class);
            actUser.putExtra("userId", userId);
            actUser.putExtra("activationCode", registerUserResponse.getActivationCode());
            startActivity(actUser);
        }

        @Override
        public void onError() {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            new AlertDialog.Builder(RegisterUserActivity.this)
                    .setTitle("Error")
                    .setMessage(registerUserResponse.getResponseMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        @Override
        public void setRegisterUserResponse(RegisterUserResponse registerUserResponse) {
            this.registerUserResponse = registerUserResponse;
        }
    };
}
