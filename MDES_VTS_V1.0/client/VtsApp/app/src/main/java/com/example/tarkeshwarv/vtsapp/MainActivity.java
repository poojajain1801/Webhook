package com.example.tarkeshwarv.vtsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.visa.cbp.external.common.DeviceInfo;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            VisaPaymentSDKImpl.initialize(getApplicationContext());
            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            DeviceInfo deviceInfo = visaPaymentSDK.getDeviceInfo("");
            int i = 10;
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }
}
