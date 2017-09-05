package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.comviva.hceservice.register.RegistrationListener;
import com.comviva.hceservice.register.Registration;

import com.comviva.mdesapp.R;

public class ActivateUserActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String userId;
    private String activationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_user);

        // Retrieve UserID & Activation Code from previous activity
        final EditText edUserId = (EditText) findViewById(R.id.editUserId);
        final EditText edActivationCode = (EditText) findViewById(R.id.editActivationCode);
        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getExtras().getString("userId");
            activationCode = edActivationCode.getText().toString();
            edUserId.setText(userId);
        }

        final Registration registration = Registration.getInstance();

        Button btnActUser = (Button) findViewById(R.id.btnActUser);
        btnActUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                registration.activateUser(edUserId.getText().toString(), edActivationCode.getText().toString(), imei, activateUserListener);
            }
        });
    }

    RegistrationListener activateUserListener = new RegistrationListener() {
        @Override
        public void onStarted() {
            progressDialog = new ProgressDialog(ActivateUserActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public void onCompleted() {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Intent actUser = new Intent(ActivateUserActivity.this, RegisterActivity.class);
            actUser.putExtra("userId", userId);
            actUser.putExtra("activationCode", activationCode);
            startActivity(actUser);
        }

        @Override
        public void onError(String errorMessage) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            new AlertDialog.Builder(ActivateUserActivity.this)
                    .setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };
}
