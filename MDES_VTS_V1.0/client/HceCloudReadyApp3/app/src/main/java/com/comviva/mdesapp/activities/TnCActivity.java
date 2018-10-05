package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.responseobject.cardmetadata.ProductConfig;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.listeners.DigitizationListener;
import com.comviva.hceservice.requestobjects.DigitizationRequestParam;
import com.comviva.hceservice.responseobject.contentguid.MediaContent;
import com.comviva.mdesapp.R;
import com.comviva.hceservice.responseobject.StepUpRequest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TnCActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tn_c);
        final TextView etTnC = (TextView) findViewById(R.id.etTnC);
        final Button btnAcceptTnC = (Button) findViewById(R.id.btnAcceptTnC);
        final Button btnDeclineTnC = (Button) findViewById(R.id.btnDeclineTnC);
        progressDialog = new ProgressDialog(TnCActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        // Get the Value of Terms And Conditions
        if (null == getIntent().getSerializableExtra("eligibilityResponse")) {
        } else {
            final ContentGuid tncContent = (ContentGuid) getIntent().getSerializableExtra("eligibilityResponse");
            MediaContent[] mediaContents = tncContent.getContent();
            if (mediaContents.length != 0) {
                byte[] data = Base64.decode(mediaContents[0].getData(), Base64.DEFAULT);
                String text = new String(data, StandardCharsets.UTF_8);
                etTnC.setText(text);
            }
        }
        final CardType cardType = (CardType) getIntent().getSerializableExtra("CardType");
        final Digitization digitization = Digitization.getInstance();
        btnAcceptTnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DigitizationRequestParam digitizationRequest = new DigitizationRequestParam();
                digitizationRequest.setCardType(cardType);
                digitizationRequest.setEmailAddress("tarkeshwar.v@mahindracomviva.com");
                digitization.digitize(digitizationRequest, new DigitizationListener() {
                    @Override
                    public void onStarted() {
                        //progressDialog.show();
                    }


                    @Override
                    public void onError(SdkError sdkError) {

                        /*if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }*/
                        new AlertDialog.Builder(TnCActivity.this)
                                .setTitle("Error")
                                .setMessage(sdkError.getMessage())
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }


                    @Override
                    public void onApproved(String instrumentID, Object object) {
                        //   Toast.makeText(TnCActivity.this, "digitization Approved" + instrumentID, Toast.LENGTH_SHORT).show();
                       /* if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }*/
                        // Request Session
                        if (cardType == CardType.MDES) {
                            new AlertDialog.Builder(TnCActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Card will Added Soon")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //     digitization.requestSession();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } else if (cardType == CardType.VTS) {
                            Intent intent = new Intent(TnCActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }


                    @Override
                    public void onDeclined() {

                       /* if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }*/
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
                    public void onRequireAdditionalAuthentication(String s, String s1, List<StepUpRequest> arrayList, Object object) {

                         /* ProductConfig productConfig = (ProductConfig)object;
                        String guid = productConfig.getCardBackgroundCombinedAssetId();

                      try {
                            digitization.getContent(cardType, guid, new GetAssetListener() {
                                @Override
                                public void onCompleted(ContentGuid contentGuid) {

                                }


                                @Override
                                public void onStarted() {

                                }


                                @Override
                                public void onError(SdkError sdkError) {

                                }
                            });
                        } catch (SdkException e) {
                            Log.e("Exception", String.valueOf(e));
                        }*/


                        final String panId = s1;
                        /*if (progressDialog.isShowing()) {
                            progressDialog.hide();
                        }*/
                        for (StepUpRequest stepUpRequest : arrayList) {
                            if (stepUpRequest.getMethod().equalsIgnoreCase("OTPSMS") || stepUpRequest.getMethod().equalsIgnoreCase("TEXT_TO_CARDHOLDER_NUMBER")) {
                                try {
                                    digitization.generateOTP(cardType, s1, stepUpRequest.getIdentifier(), new ResponseListener() {
                                        @Override
                                        public void onSuccess() {

                                            /*if (progressDialog.isShowing()) {
                                                progressDialog.hide();
                                            }*/
                                            Intent intent = new Intent(TnCActivity.this, VerifyOtp.class);
                                            intent.putExtra("CardType", cardType.name());
                                            intent.putExtra("id", panId);
                                            startActivity(intent);
                                        }


                                        @Override
                                        public void onStarted() {

                                           // progressDialog.show();
                                        }


                                        @Override
                                        public void onError(SdkError sdkError) {

                                           /* if (progressDialog.isShowing()) {
                                                progressDialog.hide();
                                            }*/
                                            Toast.makeText(TnCActivity.this, sdkError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (SdkException e) {
                                    e.printStackTrace();
                                }
                            }

                        /*new AlertDialog.Builder(TnCActivity.this)
                                .setTitle("Authentication Required")
                                .setMessage("Card digitization needs more authentication")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(TnCActivity.this, SelectAuthenticationActivity.class);
                                       // intent.putExtra("authenticationMethods", authenticationMethods);
                                       // intent.putExtra("tokenUniqueReference", tokenUniqueReference);
                                        startActivity(intent);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();*/
                        }
                    }
                    });
                }
            });

        btnDeclineTnC.setOnClickListener(new View.OnClickListener()


            {
                @Override
                public void onClick (View v){
                // T&C Declined so redirect to home
                startActivity(new Intent(TnCActivity.this, HomeActivity.class));
            }
            });
        }
    }
