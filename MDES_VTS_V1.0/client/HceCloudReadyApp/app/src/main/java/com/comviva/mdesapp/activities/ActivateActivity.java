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

import com.comviva.hceservice.digitizationApi.ActivateListener;
import com.comviva.hceservice.digitizationApi.ActivationCodeType;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.mdesapp.R;

public class ActivateActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    private void showDialog(String message) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        new AlertDialog.Builder(ActivateActivity.this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);

        final String tokenUniqueReference = getIntent().getStringExtra("tokenUniqueReference");

        final Digitization digitization = Digitization.getInstance();
        final EditText etActivationCode = (EditText) findViewById(R.id.etActivationCode);
        final Button btnSubmit = (Button) findViewById(R.id.btnSubmitActivationCode);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String activationCode = etActivationCode.getText().toString();
                if (activationCode.isEmpty()) {
                    new AlertDialog.Builder(ActivateActivity.this)
                            .setTitle("Error")
                            .setMessage("Please Enter Activation Code.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                digitization.activate(tokenUniqueReference,
                        activationCode,
                        ActivationCodeType.AUTHENTICATION_CODE,
                        new ActivateListener() {
                            @Override
                            public void onActivationStarted() {
                                progressDialog = new ProgressDialog(ActivateActivity.this);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setMessage("Please wait...");
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                            }

                            @Override
                            public void onError(String message) {
                                showDialog(message);
                            }

                            @Override
                            public void onSuccess() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                new AlertDialog.Builder(ActivateActivity.this)
                                        .setTitle("Success")
                                        .setMessage("Activation successful. Card will be Added Soon")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                digitization.requestSession();
                                                startActivity(new Intent(ActivateActivity.this, HomeActivity.class));
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                            @Override
                            public void onIncorrectCode() {
                                showDialog("Incorrect Code");
                            }

                            @Override
                            public void onRetriesExceeded() {
                                showDialog("Retries Exceeded");
                            }

                            @Override
                            public void onExpiredCode() {
                                showDialog("Activation Code Expired");
                            }

                            @Override
                            public void onIncorrectTAV() {
                                showDialog("Incorrect Code");
                            }

                            @Override
                            public void onSessionExpired() {
                                showDialog("Session Expired");
                            }
                        });
            }
        });
    }


}
