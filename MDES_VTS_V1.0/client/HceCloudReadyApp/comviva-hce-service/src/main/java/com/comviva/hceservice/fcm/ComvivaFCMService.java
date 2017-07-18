package com.comviva.hceservice.fcm;

import com.comviva.hceservice.mdes.tds.TdsNotificationData;
import com.comviva.hceservice.mdes.tds.TransactionHistory;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mcbp.api.McbpNotificationApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;

import java.util.Map;

public class ComvivaFCMService extends FirebaseMessagingService {
    public static final String MESSAGE_TAG = "notificationData";
    public static MdesCmsDedicatedWalletEventListener walletEventListener;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data;
        String strNotificationData;

        // Grab the dataReceived
        data = remoteMessage.getData();

        try {
            // TDS registration code 2 for MDES
            if(data.containsKey("registrationCode2")) {
                TdsNotificationData tdsNotificationData = new TdsNotificationData();
                tdsNotificationData.setTokenUniqueReference((String) data.get("tokenUniqueReference"));
                tdsNotificationData.setRegistrationCode2((String) data.get("registrationCode2"));
                tdsNotificationData.setTdsUrl((String) data.get("tdsUrl"));
                tdsNotificationData.setPaymentAppInstanceId((String) data.get("paymentAppInstanceId"));
                TransactionHistory.registerWithTdsFinish(tdsNotificationData);

            } else if(data.containsKey(MESSAGE_TAG)) {  // Remote Notification data for MDES
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
        } catch (Exception e) {
            return;
        }
    }

    public static void setMdesCmsDedicatedWalletEventListener(MdesCmsDedicatedWalletEventListener walletEventListener) {
        ComvivaFCMService.walletEventListener = walletEventListener;
    }

}