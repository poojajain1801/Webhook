package com.comviva.mdesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.mdesapp.R;

public class VerifyOtp extends AppCompatActivity {

    private Button verifyOtp;
    private EditText otpValueText;
    private Digitization digitization = Digitization.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        verifyOtp = (Button) findViewById(R.id.btn_verify);
        otpValueText = (EditText) findViewById(R.id.verifyOtp);
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (("").equalsIgnoreCase(otpValueText.getText().toString())) {
                    Toast.makeText(VerifyOtp.this, "Inavlid Field", Toast.LENGTH_SHORT).show();
                } else {
                    CardType cardType;
                    String type = getIntent().getStringExtra("CardType");
                    String cardID = getIntent().getStringExtra("id");
                    if (type.equalsIgnoreCase("MDES")) {
                        cardType = CardType.MDES;
                    } else {
                        cardType = CardType.VTS;
                    }
                    try {
                        digitization.verifyOTP(cardType, getIntent().getStringExtra("id"), otpValueText.getText().toString(), new ResponseListener() {
                            @Override
                            public void onSuccess() {

                                Intent intent = new Intent(VerifyOtp.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(VerifyOtp.this, "Success", Toast.LENGTH_SHORT).show();
                            }


                            @Override
                            public void onStarted() {

                                Toast.makeText(VerifyOtp.this, "onStarted", Toast.LENGTH_SHORT).show();
                            }


                            @Override
                            public void onError(SdkError sdkError) {

                                Toast.makeText(VerifyOtp.this, sdkError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
