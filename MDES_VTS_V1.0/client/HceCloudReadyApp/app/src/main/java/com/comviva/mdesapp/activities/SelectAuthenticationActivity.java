package com.comviva.mdesapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.comviva.hceservice.mdes.digitizatioApi.CardEligibilityResponse;
import com.comviva.hceservice.mdes.digitizatioApi.authentication.AuthenticationMethod;
import com.comviva.mdesapp.R;

public class SelectAuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_authentication);

        final AuthenticationMethod[] authenticationMethods = (AuthenticationMethod[]) getIntent().getSerializableExtra("authenticationMethods");

        RadioGroup rgAuthMethods = (RadioGroup) findViewById(R.id.rbgAuthMethods);
        for (int i = 0; i <= authenticationMethods.length ; i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(i + 1000);

            switch (authenticationMethods[i].getType()) {
                case CARDHOLDER_TO_USE_ISSUER_MOBILE_APP:
                    break;

                default:
                    rbn.setText(authenticationMethods[i].getValue());
            }
            rgAuthMethods.addView(rbn);
        }
    }
}
