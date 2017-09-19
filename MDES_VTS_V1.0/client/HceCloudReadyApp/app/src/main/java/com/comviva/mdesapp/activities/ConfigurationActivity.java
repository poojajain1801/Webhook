package com.comviva.mdesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.SdkException;
import com.comviva.mdesapp.R;

public class ConfigurationActivity extends AppCompatActivity {
    ComvivaSdk comvivaSdk = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final EditText etPayAppServerIp = (EditText) findViewById(R.id.etPayAppServerIp);
        final EditText etPayAppServerPort = (EditText) findViewById(R.id.etPayAppServerPort);
        final EditText etCmsDIp = (EditText) findViewById(R.id.etCmsDIp);
        final EditText etCmsDPort = (EditText) findViewById(R.id.etCmsDPort);
        final Button btnUpdateConfig = (Button) findViewById(R.id.btnUpdateConfig);

        comvivaSdk = null;
        try {
            comvivaSdk = ComvivaSdk.getInstance(null);
        } catch (SdkException e) {
            Log.d("Error", e.getMessage());
            return;
        }

        etPayAppServerIp.setText(comvivaSdk.getPaymentAppServerIP());
        etPayAppServerPort.setText(comvivaSdk.getPaymentAppServerPort());
        etCmsDIp.setText(comvivaSdk.getCmsDServerIP());
        etCmsDPort.setText(comvivaSdk.getCmsDServerPort());
        btnUpdateConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comvivaSdk.setPaymentAppServerConfiguration(etPayAppServerIp.getText().toString(),
                        Integer.parseInt(etPayAppServerPort.getText().toString()));

                comvivaSdk.setCmsDServerConfiguration(etCmsDIp.getText().toString(),
                        Integer.parseInt(etCmsDPort.getText().toString()));
                startActivity(new Intent(ConfigurationActivity.this, RegisterUserActivity.class));
            }
        });
    }
}
