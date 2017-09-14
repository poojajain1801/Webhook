package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.digitizationApi.RequestActivationCodeListener;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.comviva.mdesapp.R;

public class AuthenticationMethodActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_method);

        final String tokenUniqueReference = getIntent().getStringExtra("tokenUniqueReference");
        // Get all the authentication methods received and show them on radio button
        final AuthenticationMethod[] authenticationMethods = (AuthenticationMethod[]) getIntent().getSerializableExtra("authenticationMethods");
        final RadioGroup rgpAuthMethods = (RadioGroup) findViewById(R.id.rgpAuthenticationMethods);
        for (int i = 0; i < authenticationMethods.length; i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(i + 1000);

            switch (authenticationMethods[i].getType()) {
                case CARDHOLDER_TO_USE_ISSUER_MOBILE_APP:
                    // TODO redirect to issuer app and get authenticated
                    break;

                default:
                    rbn.setText(authenticationMethods[i].getValue());
            }
            rgpAuthMethods.addView(rbn);
        }

        Button btnAuthMethSubmit = (Button) findViewById(R.id.btnAuthMethodSubmit);
        btnAuthMethSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idSelectedAuthMethod = rgpAuthMethods.getCheckedRadioButtonId();
                if (idSelectedAuthMethod == -1) {
                    new AlertDialog.Builder(AuthenticationMethodActivity.this)
                            .setTitle("Error")
                            .setMessage("Please select activation method")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Digitization digitization = Digitization.getInstance();
                    digitization.requestActivationCode(tokenUniqueReference,
                            authenticationMethods[idSelectedAuthMethod - 1000],
                            new RequestActivationCodeListener() {
                                @Override
                                public void onStarted() {
                                    progressDialog = new ProgressDialog(AuthenticationMethodActivity.this);
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressDialog.setMessage("Please wait...");
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                }

                                @Override
                                public void onSuccess(String message) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    Intent intent = new Intent(AuthenticationMethodActivity.this, ActivateActivity.class);
                                    intent.putExtra("tokenUniqueReference", tokenUniqueReference);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(SdkError sdkError) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    new AlertDialog.Builder(AuthenticationMethodActivity.this)
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
                            });
                }
            }
        });

    }
}
