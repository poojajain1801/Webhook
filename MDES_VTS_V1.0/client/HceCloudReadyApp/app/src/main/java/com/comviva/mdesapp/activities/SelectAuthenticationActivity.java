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

import com.comviva.hceservice.mdes.digitizatioApi.Digitization;
import com.comviva.hceservice.mdes.digitizatioApi.RequestActivationCodeListener;
import com.comviva.hceservice.mdes.digitizatioApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.mdes.digitizatioApi.authentication.AuthenticationType;
import com.comviva.mdesapp.R;

public class SelectAuthenticationActivity extends AppCompatActivity {
    public static final int BASE_ID = 1000;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_authentication);

        final String tokenUniqueReference = getIntent().getStringExtra("tokenUniqueReference");
        final AuthenticationMethod[] authenticationMethods = (AuthenticationMethod[]) getIntent().getSerializableExtra("authenticationMethods");

        final RadioGroup rgAuthMethods = (RadioGroup) findViewById(R.id.rbgAuthMethods);
        final RadioButton[] rbn = new RadioButton[authenticationMethods.length];
        for (int i = 0; i < authenticationMethods.length ; i++) {
            rbn[i] = new RadioButton(this);
            rbn[i].setId(i + BASE_ID);

            switch (authenticationMethods[i].getType()) {
                case CARDHOLDER_TO_USE_ISSUER_MOBILE_APP:
                    break;

                default:
                    rbn[i].setText(authenticationMethods[i].getValue());
            }
            rgAuthMethods.addView(rbn[i]);
        }

        Button btnSubmitAuthMethod = (Button) findViewById(R.id.btnSubmitAuthMethod);
        btnSubmitAuthMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = rgAuthMethods.getCheckedRadioButtonId()-BASE_ID;
                AuthenticationType authenticationType = authenticationMethods[index].getType();
                switch (authenticationType) {
                    case TEXT_TO_CARDHOLDER_NUMBER:
                        requestActivationCode(tokenUniqueReference, authenticationMethods[index]);
                        break;

                    case CARDHOLDER_TO_CALL_AUTOMATED_NUMBER:
                        break;

                    case CARDHOLDER_TO_USE_ISSUER_MOBILE_APP:
                        break;
                }
            }
        });
    }

    public void requestActivationCode(final String tokenUniqueReference, AuthenticationMethod authenticationMethod) {
        Digitization digitization = new Digitization();
        digitization.requestActivationCode(tokenUniqueReference, authenticationMethod, new RequestActivationCodeListener() {
            @Override
            public void onReqActivationCodeStarted() {
                progressDialog = new ProgressDialog(SelectAuthenticationActivity.this);
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
                Intent intent = new Intent(SelectAuthenticationActivity.this, ActivateActivity.class);
                intent.putExtra("tokenUniqueReference", tokenUniqueReference);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(SelectAuthenticationActivity.this)
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
        });
    }
}
