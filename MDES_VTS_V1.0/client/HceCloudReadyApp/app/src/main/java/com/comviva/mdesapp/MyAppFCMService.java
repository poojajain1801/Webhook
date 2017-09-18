package com.comviva.mdesapp;

import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class MyAppFCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            ComvivaFCMService comvivaFCMService = ComvivaFCMService.getInstance();
            comvivaFCMService.onMessageReceived(remoteMessage);
        } catch (Exception e) {
        }
    }
}