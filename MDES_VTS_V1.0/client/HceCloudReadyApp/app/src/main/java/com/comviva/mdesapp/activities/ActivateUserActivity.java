package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comviva.hceservice.common.RestResponse;
import com.comviva.hceservice.register.ActivateUserListener;
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
            activationCode = getIntent().getStringExtra("activationCode").toString();
            edUserId.setText(userId);
            edActivationCode.setText(activationCode);
        }

        final Registration registration = new Registration();

        Button btnActUser = (Button) findViewById(R.id.btnActUser);
        btnActUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration.activateUser(edUserId.getText().toString(), edActivationCode.getText().toString(), activateUserListener);
            }
        });
    }

    ActivateUserListener activateUserListener = new ActivateUserListener() {
        @Override
        public void onActivationStarted() {
            progressDialog = new ProgressDialog(ActivateUserActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public void onActivationCompeted() {
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