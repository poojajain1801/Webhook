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
import android.widget.TextView;

import com.comviva.hceservice.mdes.digitizatioApi.CardEligibilityResponse;
import com.comviva.hceservice.mdes.digitizatioApi.Digitization;
import com.comviva.hceservice.mdes.digitizatioApi.DigitizationListener;
import com.comviva.hceservice.mdes.digitizatioApi.DigitizationRequest;
import com.comviva.hceservice.mdes.digitizatioApi.EligibilityReceipt;
import com.comviva.hceservice.mdes.digitizatioApi.asset.GetAssetResponse;
import com.comviva.hceservice.mdes.digitizatioApi.authentication.AuthenticationMethod;
import com.comviva.mdesapp.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class TnCActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tn_c);

        final TextView etTnC = (TextView) findViewById(R.id.etTnC);
        final Button btnAcceptTnC = (Button) findViewById(R.id.btnAcceptTnC);
        final Button btnDeclineTnC = (Button) findViewById(R.id.btnDeclineTnC);

        // Get the Value of Terms And Conditions
        final CardEligibilityResponse eligibilityResponse = (CardEligibilityResponse) getIntent().getSerializableExtra("eligibilityResponse");
        final Digitization digitization = new Digitization();
        GetAssetResponse assetResponse = digitization.getAsset(eligibilityResponse.getTermsAndConditionsAssetId());
        if (assetResponse.getResponseCode() == 200) {
            etTnC.setText(assetResponse.getMediaContents()[0].getData());
        }

        btnAcceptTnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DigitizationRequest digitizationRequest = new DigitizationRequest();
                digitizationRequest.setServiceId(eligibilityResponse.getServiceId());
                digitizationRequest.setTaskId("12345");
                digitizationRequest.setTermsAndConditionsAssetId(eligibilityResponse.getTermsAndConditionsAssetId());
                digitizationRequest.setTermsAndConditionsAcceptedTimestamp("2017-07-04T12:08:56.123-07:00");
                digitizationRequest.setEligibilityReceipt(eligibilityResponse.getEligibilityReceipt());

                digitization.digitize(digitizationRequest, new DigitizationListener() {
                    @Override
                    public void onDigitizationStarted() {
                        progressDialog = new ProgressDialog(TnCActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    public void onDigitizationCompleted() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        new AlertDialog.Builder(TnCActivity.this)
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
                    public void onApproved() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        // Request Session
                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                        new AlertDialog.Builder(TnCActivity.this)
                                .setTitle("Error")
                                .setMessage("Card will Added Soon")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        digitization.requestSession();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                    @Override
                    public void onDeclined() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        new AlertDialog.Builder(TnCActivity.this)
                                .setTitle("Error")
                                .setMessage("Request to add card is rejected")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                    @Override
                    public void onRequireAdditionalAuthentication(final String tokenUniqueReference, final AuthenticationMethod[] authenticationMethods) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        new AlertDialog.Builder(TnCActivity.this)
                                .setTitle("Authentication Required")
                                .setMessage("Card digitization needs more authentication")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(TnCActivity.this, SelectAuthenticationActivity.class);
                                        intent.putExtra("authenticationMethods", authenticationMethods);
                                        intent.putExtra("tokenUniqueReference", tokenUniqueReference);
                                        startActivity(intent);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }
        });

        btnDeclineTnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // T&C Declined so redirect to home
                digitization.requestSession();
                //startActivity(new Intent(TnCActivity.this, HomeActivity.class));
            }
        });

    }
}
