package com.comviva.mdesapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.comviva.hceservice.tds.TransactionDetails;
import com.comviva.mdesapp.R;
import com.comviva.mdesapp.TransactionHistoryAdapter;

import java.util.ArrayList;

public class TransactionHistoryMdesActivity extends AppCompatActivity {

    private TransactionHistoryAdapter transactionHistoryAdapter;
    private ArrayList<TransactionDetails> transactionDetailsArrayList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_mdes);

        transactionDetailsArrayList = (ArrayList) getIntent().getSerializableExtra("transactionDetails");

        listView = (ListView) findViewById(R.id.listview);

        transactionHistoryAdapter = new TransactionHistoryAdapter(this, transactionDetailsArrayList);
        listView.setAdapter(transactionHistoryAdapter);
    }
}
