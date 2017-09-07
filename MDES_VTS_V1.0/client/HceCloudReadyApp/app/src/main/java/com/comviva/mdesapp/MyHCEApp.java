package com.comviva.mdesapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.ComvivaWalletListener;
import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.comviva.mdesapp.activities.HomeActivity;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedPinChangeResult;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedTaskStatus;

import java.util.Random;

/**
 * Created by tarkeshwar.v on 3/21/2017.
 */

public class MyHCEApp extends Application {
    private static MyHCEApp appInstance;
    private ComvivaWalletListener mEventListener;

    @Override
    public void onCreate() {
        super.onCreate();
        ComvivaSdk.getInstance(this);
        appInstance = this;

        mEventListener = new WalletListener();
        ComvivaFCMService.setComvivaWalletListener(mEventListener);
    }

    public static MyHCEApp getInstance() {
        return appInstance;
    }

    /**
     * Publish a notification.
     *
     * @param titleResId   Resource Id of the title for the notification.
     * @param messageResId Resource Id of the message for the notification.
     */
    public static void publish(int titleResId, int messageResId) {
        String title = getInstance().getString(titleResId);
        String message = getInstance().getString(messageResId);
        publish(title, message);
    }

    /**
     * Publish a notification.
     *
     * @param titleResId Resource Id of the title for the notification.
     * @param messageRes Resource Id of the message for the notification.
     */
    public static void publish(int titleResId, String messageRes) {
        String title = getInstance().getString(titleResId);
        publish(title, messageRes);
    }

    /**
     * Publish a notification.
     *
     * @param title   The title for the notification.
     * @param message Message for the notification.
     */
    public static void publish(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appInstance);
        Notification notification = mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat)
                .setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setLargeIcon(BitmapFactory.decodeResource(getInstance().getResources(), R.drawable.notification_icon))
                .setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), notification);
    }

    //mEventListener
    private class WalletListener implements ComvivaWalletListener {
        @Override
        public boolean onRegistrationCompleted() {
            return false;
        }

        @Override
        public boolean onRegistrationFailure(final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onCardAdded(final String tokenUniqueReference) {
            publish(R.string.notification_new_card_profile_title, R.string.notification_new_card_profile_message);
            ComvivaSdk.getInstance(null).activateCard(tokenUniqueReference);
            startActivity(new Intent(MyHCEApp.this, HomeActivity.class));
            return true;
        }

        @Override
        public boolean onCardAddedFailure(final String tokenUniqueReference, final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onPaymentTokensReceived(final String tokenUniqueReference, final int numberOfCredentialReceived) {
            return false;
        }

        @Override
        public boolean onPaymentTokensReceivedFailure(final String tokenUniqueReference, final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onCardPinChanged(final String tokenUniqueReference,
                                        final MdesCmsDedicatedPinChangeResult result,
                                        final int pinTriesRemaining) {
            return false;
        }

        @Override
        public boolean onCardPinChangedFailure(final String tokenUniqueReference, final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onCardPinReset(final String tokenUniqueReference) {
                    /*McbpApplication.publish(R.string.notification_reset_pin,
                                            R.string.notification_reset_pin_received_message);
                    DataManager.INSTANCE.saveSetPinStateFromTokenUniqueReference(
                            tokenUniqueReference);*/
            return true;
        }

        @Override
        public boolean onCardPinResetFailure(final String tokenUniqueReference, final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onWalletPinChange(final MdesCmsDedicatedPinChangeResult result, final int pinTriesRemaining) {
            if ("INCORRECT_PIN".equalsIgnoreCase(result.toString())) {
                publish(R.string.notification_change_pin_title, "Change PIN failed\nTries Remaining : " + pinTriesRemaining);
            } else {
                publish(R.string.notification_change_pin_title, R.string.notification_change_pin_message);
            }
            startActivity(new Intent(MyHCEApp.getInstance().getApplicationContext(), HomeActivity.class));
            return false;
        }

        @Override
        public boolean onWalletPinChangeFailure(final int retriesRemaining, final int errorCode) {
            publish(R.string.notification_change_pin_title, "Change PIN failed");
            return false;
        }

        @Override
        public boolean onWalletPinReset() {
            //In context of wallet reset pin
                    /*DataManager.INSTANCE.setWalletPin(false);

                    McbpApplication.publish(R.string.notification_reset_pin,
                                            R.string.notification_reset_pin_wallet_received_message);*/
            return true;
        }

        @Override
        public boolean onWalletPinResetFailure(final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onCardDelete(final String tokenUniqueReference) {
            startActivity(new Intent(MyHCEApp.this, HomeActivity.class));
            return false;
        }

        @Override
        public boolean onCardDeleteFailure(final String tokenUniqueReference, final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onTaskStatusReceived(final MdesCmsDedicatedTaskStatus status) {
            return false;
        }

        @Override
        public boolean onTaskStatusReceivedFailure(final int retriesRemaining, final int errorCode) {
            return false;
        }

        @Override
        public boolean onSystemHealthCompleted() {
            return false;
        }

        @Override
        public boolean onSystemHealthFailure(final int errorCode) {
            return false;
        }

        @Override
        public void onTdsRegistrationCode2Received(String tokenUniqueReference) {
            publish("Tds Registration", "TdsRegistrationCode2 received for Card " + tokenUniqueReference);
        }

        @Override
        public void onTdsRegistrationSuccess(String tokenUniqueReference) {
            publish("Tds Registration", "Tds Registration Successful for Card " + tokenUniqueReference);
        }

        public void onTdsRegistrationError(String tokenUniqueReference, final String errorMessage) {
            publish("Tds Registration", "Tds Registration failed for Card " + tokenUniqueReference);
        }

        @Override
        public void onTdsNotificationReceived(String tokenUniqueReference) {
            publish("Transaction Notification", "Transaction Notification for Card\n" + tokenUniqueReference);
        }
    }


}
