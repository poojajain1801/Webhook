package com.comviva.mdesapp.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.comviva.hceservice.mdes.digitizatioApi.Digitization;
import com.comviva.mdesapp.R;

public class ChangePinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        final EditText etOldPin = (EditText) findViewById(R.id.etOldPin);
        final EditText etNewPin = (EditText) findViewById(R.id.etNewPin);
        final Button btnSubmit = (Button) findViewById(R.id.btnChangePinSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPin = etOldPin.getText().toString();
                String newPin = etNewPin.getText().toString();

                // Validate PINs
                if(oldPin.isEmpty() || newPin.isEmpty()) {
                    new AlertDialog.Builder(ChangePinActivity.this)
                            .setTitle("Success")
                            .setMessage("Please enter PIN")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                Digitization digitization = new Digitization();
                digitization.changePin(null, oldPin, newPin);
            }
        });

    }
}
