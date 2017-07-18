package com.comviva.mdesapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.comviva.hceservice.mdes.digitizatioApi.CardEligibilityResponse;
import com.comviva.hceservice.mdes.tds.TransactionDetails;
import com.comviva.mdesapp.R;

public class TransactionHistoryMdesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_mdes);

        final TransactionDetails[] transactionDetails = (TransactionDetails[]) getIntent().getSerializableExtra("transactionDetails");
    }
}
