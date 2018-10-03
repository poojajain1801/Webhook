package com.comviva.hceservice.digitizationApi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.listeners.ResponseListener;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.Constants;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;
import java.util.ArrayList;

/**
 * Service performs replenishment when transaction credential is expired or number of allowed transactions is consumed.
 * Created by amit.randhawa on 29-Aug-17.
 */
public class ActiveAccountManagementService extends Service {
    private VisaPaymentSDK visaPaymentSDK;

    private void callReplenish(final TokenKey tokenKey) {
        if (tokenKey != null) {
            try {
                TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);


                    Digitization digitization = Digitization.getInstance();
                    digitization.replenishTransactionCredential(paymentCard, new ResponseListener() {
                        @Override
                        public void onStarted() {
                            // Replenish started
                        }

                        @Override
                        public void onSuccess() {
                            //On Success

                        }

                        @Override
                        public void onError(SdkError sdkError) {

                            //on Error

                        }
                    });


            } catch (TokenInvalidException e ) {
                Log.d("ComvivaSdkError", e.getMessage());
            } catch (Exception e) {
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