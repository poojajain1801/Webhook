package com.comviva.mdesapp;

import com.comviva.hceservice.common.ComvivaWalletListener;
import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.comviva.hceservice.mdes.tds.TdsNotificationData;
import com.comviva.hceservice.mdes.tds.TransactionHistory;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpNotificationApi;
import com.mastercard.mcbp.api.McbpWalletApi;

import java.util.Map;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class MyAppFCMService extends FirebaseMessagingService {
    private static ComvivaWalletListener walletEventListener;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            ComvivaFCMService comvivaFCMService = ComvivaFCMService.getInstance();
            comvivaFCMService.onMessageReceived(remoteMessage);
        } catch (Exception e) {
            return;
        }
    }
}