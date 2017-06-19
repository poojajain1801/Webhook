package com.comviva.mdesapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comviva.hceservice.common.RestResponse;
import com.comviva.hceservice.register.ActivateUserListener;

import com.comviva.hceservice.register.DeviceInfo;
import com.comviva.hceservice.register.RegisterParam;
import com.comviva.hceservice.register.Registration;
import com.comviva.mdesapp.R;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends Activity {
    private ProgressDialog progressDialog;

    private boolean isNfcEnabled(Context context) {
        PackageManager pm = context.getPackageManager();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        return pm.hasSystemFeature(PackageManager.FEATURE_NFC) || (null != nfcAdapter);
    }

    private DeviceInfo getDeviceInfoJson(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setDeviceName(Build.MODEL);
        deviceInfo.setDeviceType(Build.MANUFACTURER);
        deviceInfo.setImei(deviceId);
        deviceInfo.setMsisdn(Build.DEVICE);
        deviceInfo.setNfcCapable((isNfcEnabled(context) ? "true" : "false"));
        deviceInfo.setOsName("ANDROID");
        deviceInfo.setSerialNumber(Build.FINGERPRINT);
        deviceInfo.setOsVersion(Build.VERSION.RELEASE);
        return deviceInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Retrieve UserID & Activation Code from previous activity
        final EditText edUserId = (EditText) findViewById(R.id.editUserId);
        final EditText edActivationCode = (EditText) findViewById(R.id.editActivationCode);
        final EditText editMobilePin = (EditText) findViewById(R.id.editMobilePin);
        final EditText editDeviceName = (EditText) findViewById(R.id.editDeviceName);
        if (getIntent().hasExtra("userId")) {
            edUserId.setText(getIntent().getExtras().getString("userId"));
            edActivationCode.setText(getIntent().getStringExtra("activationCode").toString());
            edActivationCode.setEnabled(false);
        }

        final RegisterParam registerParam = new RegisterParam();
        registerParam.setDeviceInfo(getDeviceInfoJson(getApplicationContext()));
        registerParam.setPaymentAppId("ComvivaWallet");
        registerParam.setPublicKeyFingerprint("1BBEFAA95B26B9E82E3FDD37B20050FC782B2F229A8F8BCBBCB6AA6ABE4C851E");

        final Registration registration = new Registration();

        Button btnReg = (Button) findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerParam.setUserId(edUserId.getText().toString());
                registerParam.setActivationCode(edActivationCode.getText().toString());
                registerParam.setMobilePin(editMobilePin.getText().toString());
                registerParam.setDeviceName(editDeviceName.getText().toString());
                registerParam.setFcmRegistrationId(FirebaseInstanceId.getInstance().getToken());
                registration.registerDevice(registerParam, regDeviceListener);
            }
        });
    }

    ActivateUserListener regDeviceListener = new ActivateUserListener() {
        @Override
        public void onActivationStarted() {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public void onActivationCompeted() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        }

        @Override
        public void onError(String errorMessage) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            new AlertDialog.Builder(RegisterActivity.this)
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
