package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.comviva.hceservice.mdes.digitizatioApi.CardEligibilityRequest;
import com.comviva.hceservice.mdes.digitizatioApi.CardEligibilityResponse;
import com.comviva.hceservice.mdes.digitizatioApi.CheckCardEligibilityListener;
import com.comviva.hceservice.mdes.digitizatioApi.Digitization;
import com.comviva.hceservice.mdes.digitizatioApi.asset.GetAssetResponse;
import com.comviva.hceservice.register.RegisterParam;
import com.comviva.mdesapp.R;

public class AddCardActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        final EditText etPan = (EditText) findViewById(R.id.editPan);
        final EditText etCvc = (EditText) findViewById(R.id.editCvc);
        final EditText etExpMonth = (EditText) findViewById(R.id.editExpMonth);
        final EditText etExpYear = (EditText) findViewById(R.id.editExpYear);
        final EditText etCardHolderName = (EditText) findViewById(R.id.editCardHolderName);
        final CardEligibilityRequest cardEligibilityRequest = new CardEligibilityRequest();
        cardEligibilityRequest.setAccountNumber(etPan.getText().toString());
        cardEligibilityRequest.setSecurityCode(etCvc.getText().toString());
        cardEligibilityRequest.setExpiryMonth(etExpMonth.getText().toString());
        cardEligibilityRequest.setExpiryYear(etExpYear.getText().toString());
        cardEligibilityRequest.setCardholderName(etCardHolderName.getText().toString());

        Button digiCard = (Button) findViewById(R.id.btnDigCard);
        digiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Digitization digitization = new Digitization();
                digitization.checkCardEligibility(cardEligibilityRequest, new CheckCardEligibilityListener() {
                    @Override
                    public void onCheckEligibilityStarted() {
                        progressDialog = new ProgressDialog(AddCardActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    public void onCheckEligibilityCompleted() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCheckEligibilityError(String message) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        new AlertDialog.Builder(AddCardActivity.this)
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
                    public void onTermsAndConditionsRequired(CardEligibilityResponse cardEligibilityResponse) {
                        try {
                            Intent intent = new Intent(AddCardActivity.this, TnCActivity.class);
                            intent.putExtra("eligibilityResponse", cardEligibilityResponse);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
}
