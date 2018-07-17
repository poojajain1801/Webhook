package com.comviva.hceservice.digitizationApi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.util.ResponseListener;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.Constants;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Service performs replenishment when transaction credential is expired or number of allowed transactions is consumed.
 * Created by amit.randhawa on 29-Aug-17.
 */
public class ActiveAccountManagementService extends Service {
    private VisaPaymentSDK visaPaymentSDK;

    private void publish(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        Notification notification = mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat)
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), notification);
    }

    private void callReplenish(final TokenKey tokenKey) {
        if (tokenKey != null) {
            try {
                TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);

                ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);

                    Digitization digitization = Digitization.getInstance();
                    digitization.replenishTransactionCredential(paymentCard, new ResponseListener() {
                        @Override
                        public void onStarted() {

                        }

                        @Override
                        public void onSuccess() {

                          //  publish("Replenish " + paymentCard.getCardLast4Digit(), "Transaction Credentials for card ending with " + paymentCard.getCardLast4Digit() + " successfully received ");
                        }

                        @Override
                        public void onError(SdkError sdkError) {

                        //    publish("Replenish " + paymentCard.getCardLast4Digit(), "Transaction Credentials for card ending with " + paymentCard.getCardLast4Digit() + " failed to receive");
                        }
                    });


            } catch (TokenInvalidException | SdkException e) {
                Log.d("ComvivaSdkError", e.getMessage());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.d("ActiveAccMgmntService", "Replenishment Service Started");
        try {
            if (visaPaymentSDK == null) {
                visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            }
            if (intent != null && intent.hasExtra(Constants.REPLENISH_TOKENS_KEY)) {
                ArrayList<TokenKey> tokens = intent.getParcelableArrayListExtra(Constants.REPLENISH_TOKENS_KEY);
                for (final TokenKey tokenKey : tokens) {

                    callReplenish(tokenKey);

                }
            }
            return START_NOT_STICKY;
        }catch(Exception e){
            Log.d("onStartCommand",e.getMessage());
            return START_NOT_STICKY;
        }

    }
}