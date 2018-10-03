package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.listeners.CheckCardEligibilityListener;
import com.comviva.hceservice.responseobject.cardmetadata.CardData;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;
import com.comviva.hceservice.digitizationApi.ConsumerEntryMode;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;
import com.comviva.hceservice.requestobjects.CardEligibilityRequestParam;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.digitizationApi.PanSource;
import com.comviva.hceservice.responseobject.contentguid.MediaContent;
import com.comviva.mdesapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class AddCardActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);


        final EditText etPan = (EditText) findViewById(R.id.editPan);
        final EditText etExpMonth = (EditText) findViewById(R.id.editExpMonth);
        final EditText etExpYear = (EditText) findViewById(R.id.editExpYear);
        final EditText etCardHolderName = (EditText) findViewById(R.id.editCardHolderName);
        final CardEligibilityRequestParam cardEligibilityRequest = new CardEligibilityRequestParam();

        Button digiCard = (Button) findViewById(R.id.btnDigCard);
        digiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardEligibilityRequest.setAccountNumber(etPan.getText().toString());
               // cardEligibilityRequest.setSecurityCode(etCvc.getText().toString());
                cardEligibilityRequest.setExpiryMonth(etExpMonth.getText().toString());
                cardEligibilityRequest.setExpiryYear(etExpYear.getText().toString());
                cardEligibilityRequest.setCardholderName(etCardHolderName.getText().toString());
                cardEligibilityRequest.setConsumerEntryMode(ConsumerEntryMode.KEYENTERED);
                cardEligibilityRequest.setLocale("en-US");
                cardEligibilityRequest.setPanSource(PanSource.MANUALLYENTERED);
                SharedPreferences userPref = getSharedPreferences("user_details", MODE_PRIVATE);
                cardEligibilityRequest.setUserId(userPref.getString("user_id", null));

                final Digitization digitization = Digitization.getInstance();
                digitization.checkCardEligibility(cardEligibilityRequest, new CheckCardEligibilityListener() {
                    @Override
                    public void onStarted() {
                        progressDialog = new ProgressDialog(AddCardActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }



                    @Override
                    public void onError(SdkError sdkError) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        new AlertDialog.Builder(AddCardActivity.this)
                                .setTitle("Error")
                                .setMessage(sdkError.getMessage())
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        /* continue*/}
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                    @Override
                    public void onTermsAndConditionsRequired(ContentGuid contentGuid) {


                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            if(null == contentGuid){

                                Intent intent = new Intent(AddCardActivity.this, TnCActivity.class);
                                startActivity(intent);
                            }else {
                                if (contentGuid == null) {
                                } else {
                                    try {
                                        Intent intent = new Intent(AddCardActivity.this, TnCActivity.class);
                                        intent.putExtra("eligibilityResponse", contentGuid);
                                        intent.putExtra("CardType", CardType.checkCardType(etPan.getText().toString()));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }




                });



            /*    digitization.getContent(CardType.MDES,"95d4cd38-36fc-4b26-8795-06a3b00acf3b", new GetAssetListener() {
                    @Override
                    public void onStarted() {

                    }


                    @Override
                    public void onCompleted(ContentGuid contentGuid) {


                    }


                    @Override
                    public void onError(String s) {

                    }
                });*/
            }
        });

    }




    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(AddCardActivity.this,HomeActivity.class));
        super.onBackPressed();
    }
}
