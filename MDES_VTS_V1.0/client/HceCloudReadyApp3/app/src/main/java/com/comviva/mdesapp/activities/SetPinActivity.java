package com.comviva.mdesapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.mdesapp.R;

public class SetPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        Button btnSetPin = (Button) findViewById(R.id.set_pin_btn);
        final EditText etSetPin = (EditText) findViewById(R.id.set_pin_edt);

        btnSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = etSetPin.getText().toString();
                if (pin.isEmpty() || pin.length() < 4 || pin.length() > 8) {
                    Toast.makeText(SetPinActivity.this, "Request for set pin placed ", Toast.LENGTH_SHORT).show();
                }

                Digitization digitization = Digitization.getInstance();
               // digitization.setPin(null, etSetPin.getText().toString());
            }
        });
    }
}
