package com.comviva.mdesapp;

import android.util.Log;

import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class MyAppFCMService extends FirebaseMessagingService {
    public static boolean vtcFcmevrntOccered = false;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            ComvivaFCMService comvivaFCMService = ComvivaFCMService.getInstance();
            Log.d("onMessageReceived","Notification Recived and send to the comvivaSDK for processing");
            comvivaFCMService.onMessageReceived(remoteMessage);
            Map data = remoteMessage.getData();
            if (data.containsKey("TYPE"))
            {
                String type = data.get("TYPE").toString();
                if (type.equalsIgnoreCase("VTS"))
                {
                    vtcFcmevrntOccered = true;
                }
            }

        } catch (Exception e) {
        }
    }
}