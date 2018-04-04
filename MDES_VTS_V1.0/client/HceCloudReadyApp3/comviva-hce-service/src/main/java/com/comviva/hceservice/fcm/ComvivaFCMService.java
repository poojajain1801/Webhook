package com.comviva.hceservice.fcm;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.ComvivaWalletListener;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.digitizationApi.CardMetaData;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.tds.TdsNotificationData;
import com.comviva.hceservice.tds.TransactionHistory;
import com.comviva.hceservice.tds.TransactionHistoryListener;
import com.comviva.hceservice.util.GetCardMetaDataListener;
import com.comviva.hceservice.util.ResponseListener;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpNotificationApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.visa.cbp.sdk.facade.util.ContextHelper.getApplicationContext;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class ComvivaFCMService {

    private static final String MESSAGE_TAG = "notificationData";
    private static final String KEY_NOTIFICATION_TYPE = "notificationType";
    private static final String OPERATION = "OPERATION";
    private static final String TOKEN_STATUS_UPDATED = "TOKEN_STATUS_UPDATED";
    private static final String KEY_STATUS_UPDATED = "KEY_STATUS_UPDATED";
    private static final String UPDATE_CARD_META_DATA = "UPDATE_CARD_METADATA";
    private static final String UPDATE_TXN_HISTORY = "UPDATE_TXN_HISTORY";
    private static final String TYPE_TDS_REGISTRATION_NOTIFICATION = "notificationTdsRegistration";
    private static final String TYPE_TDS_NOTIFICATION = "notificationTds";
    private Digitization digitization;

    private static ComvivaWalletListener walletEventListener;
    private static ComvivaFCMService comvivaFCMService;
    private static Application applicationContext;

    /**
     * Returns Singleton Instance of this class.
     *
     * @return ComvivaFCMService instance
     */
    public static ComvivaFCMService getInstance(Application application) {
        if (comvivaFCMService == null) {
            applicationContext = application;
            comvivaFCMService = new ComvivaFCMService();
        }
        return comvivaFCMService;
    }

    /**
     * Invoke this API when FCM notification comes.
     *
     * @param remoteMessage RemoteMessage object received through FCM notification
     */
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data;
        String strNotificationData;

        // Grab the dataReceived
        data = remoteMessage.getData();

        try {
            if (data.containsKey("TYPE")) {
                String type = data.get("TYPE").toString();

                if (type.equalsIgnoreCase("MDES")) {

                    if (data.containsKey(KEY_NOTIFICATION_TYPE)) {
                        String notificationType = data.get(KEY_NOTIFICATION_TYPE).toString();
                        if (notificationType.equalsIgnoreCase(TYPE_TDS_REGISTRATION_NOTIFICATION)) {
                            // New TDS registration
                            String tokenUniqueRef = (String) data.get("tokenUniqueReference");
                            walletEventListener.onTdsRegistrationCode2Received(McbpCardApi.getDisplayablePanDigits(tokenUniqueRef));
                            TdsNotificationData tdsNotificationData = new TdsNotificationData();
                            tdsNotificationData.setTokenUniqueReference(tokenUniqueRef);
                            tdsNotificationData.setRegistrationCode2((String) data.get("registrationCode2"));
                            tdsNotificationData.setTdsUrl((String) data.get("tdsUrl"));
                            tdsNotificationData.setPaymentAppInstanceId((String) data.get("paymentAppInstanceId"));
                            TransactionHistory.registerWithTdsFinish(tdsNotificationData);
                        } else if (notificationType.equalsIgnoreCase(TYPE_TDS_NOTIFICATION)) {
                            // New TDS notification
                            walletEventListener.onTdsNotificationReceived((String) data.get("tokenUniqueReference"));
                        }
                    } else if (data.containsKey(MESSAGE_TAG)) {  // Remote Notification data for MDES initiated from CMS-d
                        strNotificationData = data.get(MESSAGE_TAG).toString();

                        // If there are no listeners to wallet events, create one that notifies the user via a local
                        // notification about what has happened
                        boolean isListening = false;
                        if (McbpWalletApi.getWalletEventListeners().size() == 0) {
                            isListening = true;
                            McbpWalletApi.addWalletEventListener(walletEventListener);
                        }

                        // Allow the SDK to process the message with the CMS system
                        if (strNotificationData != null && !strNotificationData.isEmpty()) {
                            McbpNotificationApi.handleNotification(strNotificationData);
                        }

                        // Stop listener if we were listening
                        if (isListening) {
                            //McbpWalletApi.removeWalletEventListener(walletEventListener);
                        }
                    }
                } else if (type.equalsIgnoreCase("VTS")) { // Remote Notifications for VTS
                    digitization = Digitization.getInstance();
                    ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(applicationContext);
                    if (data.containsKey(OPERATION) && data.get(OPERATION).toString().equalsIgnoreCase(TOKEN_STATUS_UPDATED)) {
                        String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        digitization.getTokenStatus(paymentCard, new ResponseListener() {
                            @Override
                            public void onStarted() {
                                System.out.println("Started");
                            }

                            @Override
                            public void onSuccess() {
                                System.out.println("Success");
                                Log.d("VTS NOtification","getTokenstatus Success");
                              //  publish("Token Status Updated " + paymentCard.getCardLast4Digit(), "Card status updated for the card number ending with " + paymentCard.getCardLast4Digit());
                            }

                            @Override
                            public void onError(SdkError sdkError) {
                                System.out.println("Error");
                            }
                        });

                        // call get Token status
                    } else if (data.containsKey(OPERATION) && data.get(OPERATION).toString().equalsIgnoreCase(KEY_STATUS_UPDATED)) {
                        String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        digitization.replenishTransactionCredential(paymentCard, new ResponseListener() {
                            @Override
                            public void onStarted() {

                            }

                            @Override
                            public void onSuccess() {
                                Log.d("VTS NOtification","replenishTransactionCredential Successful");
                               // publish("Token Status Updated " + paymentCard.getCardLast4Digit(), "Card successfully replenish for the card number ending with " + paymentCard.getCardLast4Digit());

                            }

                            @Override
                            public void onError(SdkError sdkError) {

                            }
                        });
                        //Call replenish, conform replenish and replenish ODA data
                    } else if (data.containsKey(OPERATION) && data.get(OPERATION).toString().equalsIgnoreCase(UPDATE_CARD_META_DATA)) {
                        String vPanEnrollmentID = data.get("vPanEnrollmentId").toString();
                       /* VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vPanEnrollmentID);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);*/
                        digitization.getCardMetaData(vPanEnrollmentID, new GetCardMetaDataListener() {
                            @Override
                            public void onStarted() {
                            }

                            @Override
                            public void onSuccess(CardMetaData cardMetaData) {
                                Log.d("VTS NOtification","replenishTransactionCredential Successful");
                               // publish("Meta Data Updated " , "Success" );

                            }

                            @Override
                            public void onError(SdkError sdkError) {
                            }
                        });
                        //UPDATE_CARD_METADATA
                    } else if (data.containsKey(OPERATION) && data.get(OPERATION).toString().equalsIgnoreCase(UPDATE_TXN_HISTORY)) {
                        String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        TransactionHistory.getTransactionHistory(paymentCard, 4, new TransactionHistoryListener() {
                            @Override
                            public void onSuccess(ArrayList transactionInfo) {

                            }

                            @Override
                            public void onStarted() {
                            }

                            @Override
                            public void onError(SdkError sdkError) {
                            }
                        });
                        //Call getTransctionHistory and update txnHistory
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            return;
        }
    }

    /***
     * Set Wallet Event Listener.
     * @param walletEventListener Wallet Event Listener
     */
    public static void setComvivaWalletListener(ComvivaWalletListener walletEventListener) {
        ComvivaFCMService.walletEventListener = walletEventListener;
    }

    /**
     * Returns Wallet Event Listener
     *
     * @return Wallet Event Listener
     */
    public static ComvivaWalletListener getWalletEventListener() {
        return walletEventListener;
    }

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


}