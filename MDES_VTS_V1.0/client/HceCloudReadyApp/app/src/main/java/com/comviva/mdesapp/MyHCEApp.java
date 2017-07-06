package com.comviva.mdesapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.comviva.mdesapp.activities.HomeActivity;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedPinChangeResult;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedTaskStatus;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;

import java.util.Random;

/**
 * Created by tarkeshwar.v on 3/21/2017.
 */

public class MyHCEApp extends Application {
    private static MyHCEApp appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        ComvivaHce.getInstance(this);
        appInstance = this;

        ComvivaFCMService.setMdesCmsDedicatedWalletEventListener(mEventListener);
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
        publish(titleResId, getInstance().getString(messageResId));
    }

    /**
     * Publish a notification.
     *
     * @param titleResId   Resource Id of the title for the notification.
     * @param messageResId Resource Id of the message for the notification.
     */
    public static void publish(int titleResId, String message) {

        String title = getInstance().getString(titleResId);

        // Start building up the notification
        Notification.Builder builder = new Notification.Builder(getInstance())
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(message).setLargeIcon(
                        BitmapFactory.decodeResource(getInstance().getResources(),
                                R.drawable.notification_icon));

        // Building the notification is SDK version dependant
        Notification notification = builder.build();

        // Grab the notification service and send it
        NotificationManager notificationManager = (NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), notification);
    }


    private final MdesCmsDedicatedWalletEventListener mEventListener =
            new MdesCmsDedicatedWalletEventListener() {

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
                    publish(R.string.notification_new_card_profile_title,
                            R.string.notification_new_card_profile_message);
                    String digitizedCardId = null;
                    try {
                        digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService().getCardIdFromTokenUniqueReference(tokenUniqueReference);
                    } catch (InvalidInput invalidInput) {
                        invalidInput.printStackTrace();
                    }
                    // If it's MDES mode then we want to remove the stored preferences record of the
                    // currently digitizing card
                    if (McbpInitializer.getInstance().getRemoteProtocol() == McbpInitializer.RemoteProtocol.Mdes) {
                        /*DataManager.INSTANCE.clearDigitizedCardDetails();
                        DataManager.INSTANCE.saveSetPinState(digitizedCardId);*/
                    }
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
                    if("INCORRECT_PIN".equalsIgnoreCase(result.toString())) {
                        publish(R.string.notification_change_pin_title,  "Change PIN failed\nTries Remaining : " + pinTriesRemaining);
                    } else {
                        publish(R.string.notification_change_pin_title,  R.string.notification_change_pin_message);
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
            };
}
