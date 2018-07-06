package com.comviva.mdesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by amit.randhawa on 23-04-2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("operation");
        System.out.println("Operation" + message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


        if(intent.hasExtra("cardStatus"))
        {

            String message1 = intent.getStringExtra("cardStatus");
            System.out.println("cardStatus" + message1);


        }

    }

}
