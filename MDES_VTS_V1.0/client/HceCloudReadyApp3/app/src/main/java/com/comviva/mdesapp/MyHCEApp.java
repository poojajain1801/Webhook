package com.comviva.mdesapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by tarkeshwar.v on 3/21/2017.
 */

    public class MyHCEApp extends Application {
    private static MyHCEApp appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("app_db")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
       // mEventListener = new WalletListener();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        config.shouldDeleteRealmIfMigrationNeeded();
        Realm.setDefaultConfiguration(config);
     //   ComvivaFCMService.setComvivaWalletListener(mEventListener);
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


    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
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

}
