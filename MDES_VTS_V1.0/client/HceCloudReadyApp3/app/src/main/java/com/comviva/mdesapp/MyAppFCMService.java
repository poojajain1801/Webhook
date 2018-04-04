package com.comviva.mdesapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import static com.visa.cbp.sdk.facade.util.ContextHelper.getApplicationContext;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class MyAppFCMService extends FirebaseMessagingService {
    public static boolean vtcFcmevrntOccered = false;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {

           // publish("Notification", "recived");
            ComvivaFCMService comvivaFCMService = ComvivaFCMService.getInstance(getApplication());
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