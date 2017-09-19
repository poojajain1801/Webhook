package com.comviva.hceservice.common.database;


import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.fcm.RnsInfo;

/**
 * Common Database containing SDK initialization data.
 */
public interface CommonDb {
    /**
     * Initialize SDK with initialization data.
     * @param comvivaSdkInitData SDK Initialization Data
     */
    void initializeComvivaSdk(ComvivaSdkInitData comvivaSdkInitData);

    /**
     * Set Remote Notification Service information. In case of FCM, registration token.
     * @param rnsInfo RNS Info ()
     */
    void setRnsInfo(RnsInfo rnsInfo);

    /**
     * Returns SDK initialization data.
     * @return Initialization Data.
     */
    ComvivaSdkInitData getInitializationData();

    /**
     * Clears all tables from database and SDK state changes to initialized state.
     */
    void resetDatabase();

    /**
     * Set default card.
     * @param paymentCard Payment Card to set as default
     * @return <code>true </code>If default card is set successfully.<br/>
     *         <code>false </code>Error while default card is not set.
     */
    boolean setDefaultCard(PaymentCard paymentCard);

    /**
     * Returns default card's unique ID.
     * @return Unique Card Id of the default card.
     */
    String getDefaultCardUniqueId();

    /**
     * Returns payment card object set as default card.
     * @return Default Card
     */
    PaymentCard getDefaultCard();
}
