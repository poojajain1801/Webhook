package com.comviva.hceservice.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.comviva.hceservice.common.ComvivaWalletListener;
import com.comviva.hceservice.mdes.tds.TdsNotificationData;
import com.comviva.hceservice.mdes.tds.TransactionHistory;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpNotificationApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;

import java.util.Map;
import java.util.Random;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class ComvivaFCMService {
    private static final String MESSAGE_TAG = "notificationData";
    private static final String KEY_NOTIFICATION_TYPE = "notificationType";
    private static final String TYPE_TDS_REGISTRATION_NOTIFICATION = "notificationTdsRegistration";
    private static final String TYPE_TDS_NOTIFICATION = "notificationTds";

    private static ComvivaWalletListener walletEventListener;
    private static ComvivaFCMService comvivaFCMService;

    /**
     * Returns Singleton Instance of this class.
     * @return ComvivaFCMService instance
     */
    public static ComvivaFCMService getInstance() {
        if(comvivaFCMService == null) {
            comvivaFCMService = new ComvivaFCMService();
        }
        return comvivaFCMService;
    }

    /**
     * Invoke this API when FCM notification comes.
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

                }
            }
        } catch (Exception e) {
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
     * @return Wallet Event Listener
     */
    public static ComvivaWalletListener getWalletEventListener() {
        return walletEventListener;
    }

}