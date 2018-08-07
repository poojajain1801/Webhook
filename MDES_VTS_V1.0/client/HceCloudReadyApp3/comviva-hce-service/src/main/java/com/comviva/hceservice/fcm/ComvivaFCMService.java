package com.comviva.hceservice.fcm;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

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
import com.comviva.hceservice.util.TokenDataUpdateListener;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpNotificationApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.TokenStatus;

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
    /** The Constant SUSUPEND. */
    public static final String SUSUPEND_USER = "SUSPENDUSER";
    /** The Constant SUSUPEND. */
    public static final String DELETE_USER = "DELETEUSER";
    /** The Constant SUSUPEND. */
    public static final String UNSUSPEND_USER = "UNSUSPENDUSER";

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
                        final String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        final VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        digitization.getTokenStatus(paymentCard, new TokenDataUpdateListener() {

                            @Override
                            public void onSuccess(String newStatus) {
                                broadcastMessage(TOKEN_STATUS_UPDATED,paymentCard.getCardLast4Digit(),newStatus);
                            }

                            @Override
                            public void onError(SdkError sdkError) {

                            }

                            @Override
                            public void onStarted() {

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

                        broadcastMessage(UPDATE_TXN_HISTORY,paymentCard.getCardLast4Digit(),null);
                        Intent intent = new Intent("comviva_broadcast");
                        intent.putExtra("cardLast4", paymentCard.getCardLast4Digit());
                        intent.putExtra("operation", "UPDATE_TXN");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        Log.d("Comviva Broadcast","Broadcast Successful");
                     /*   TransactionHistory.getTransactionHistory(paymentCard, 4, new TransactionHistoryListener() {
                            @Override
                            public void onSuccess(ArrayList transactionInfo) {

                            }

                            @Override
                            public void onStarted() {
                            }

                            @Override
                            public void onError(SdkError sdkError) {
                            }
                        });*/
                        //Call getTransctionHistory and update txnHistory
                    }
                }else if(type.equalsIgnoreCase("ALL"))
                {
                    ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(applicationContext);
                    VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                    ArrayList<PaymentCard> paymentCards = ComvivaSdk.getInstance(applicationContext).getAllCards();
                    for(int i = 0; i<paymentCards.size();i++)
                    {
                        if(paymentCards.get(i).getCardType().toString().equalsIgnoreCase("VTS")) {
                            if(data.containsKey(OPERATION)) {
                                if(data.get(OPERATION).toString().equalsIgnoreCase(SUSUPEND_USER))
                                {
                                    visaPaymentSDK.updateTokenStatus(((TokenData) paymentCards.get(i).getCurrentCard()).getTokenKey(), TokenStatus.SUSPENDED);
                                }else if(data.get(OPERATION).toString().equalsIgnoreCase(UNSUSPEND_USER))
                                {
                                    visaPaymentSDK.updateTokenStatus(((TokenData) paymentCards.get(i).getCurrentCard()).getTokenKey(), TokenStatus.RESUME);
                                }
                            }
                        }else if((paymentCards.get(i).getCardType().toString().equalsIgnoreCase("MDES")))
                        {

                        }
                    }
                    if(data.containsKey(OPERATION))
                    {
                        broadcastMessage(data.get(OPERATION).toString(),null,null);
                    }
                     if(data.get(OPERATION).toString().equalsIgnoreCase(DELETE_USER))
                    {
                        comvivaSdk.resetDevice();
                        broadcastMessage(DELETE_USER,null,null);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            return;
        }
    }


    private void broadcastMessage(String operation, String last4, String status)
    {
        //Intent intent = new Intent("comviva_broadcast");
        Intent intent = new Intent();
        intent.setAction("comviva_broadcast");

        switch (operation)
        {
            case DELETE_USER:
                intent.putExtra("operation", DELETE_USER);
                break;
            case TOKEN_STATUS_UPDATED:
                intent.putExtra("cardLast4", last4);
                intent.putExtra("operation", "CARD_STATUS_UPDATE");
                intent.putExtra("cardStatus", status);
                break;
            case UPDATE_TXN_HISTORY:
                intent.putExtra("cardLast4", last4);
                intent.putExtra("operation", "UPDATE_TXN");
                break;
            case SUSUPEND_USER:
                intent.putExtra("operation", SUSUPEND_USER);
                break;

            case UNSUSPEND_USER:
                intent.putExtra("operation",UNSUSPEND_USER );
                break;
            default:
                break;
        }
        getApplicationContext().sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

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



}