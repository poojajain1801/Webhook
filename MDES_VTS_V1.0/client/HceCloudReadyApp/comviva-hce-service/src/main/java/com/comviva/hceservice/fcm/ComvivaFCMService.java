package com.comviva.hceservice.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mcbp.api.McbpNotificationApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedPinChangeResult;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedTaskStatus;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;
import com.mastercard.mcbp.remotemanagement.mdes.RnsMessage;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ComvivaFCMService extends FirebaseMessagingService {
    public static final String MESSAGE_TAG = "notificationData";
    public static MdesCmsDedicatedWalletEventListener walletEventListener;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data;
        String strNotificationData;
        try {
            // Grab the dataReceived
            data = remoteMessage.getData();
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
                McbpWalletApi.removeWalletEventListener(walletEventListener);
            }
        } catch (Exception e) {
            return;
        }
    }

    public static void setMdesCmsDedicatedWalletEventListener(MdesCmsDedicatedWalletEventListener walletEventListener) {
        ComvivaFCMService.walletEventListener = walletEventListener;
    }
}