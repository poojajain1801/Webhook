package com.comviva.hceservice.common.database;


import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.RmPendingTask;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.tds.TdsRegistrationData;

/**
 * Common Database containing SDK initialization data.
 */
public interface CommonDb {
    /**
     * Task ID for Provision
     */
    //String RM_T_ID_001 = "RM_T_ID_001";
    /**
     * Task ID for DELETE TOKEN
     */
    //String RM_T_ID_002 = "RM_T_ID_002";
    /**
     * Task ID for Replenish TOKEN
     */
    //String RM_T_ID_003 = "RM_T_ID_003";

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
     * Returns Pending Task
     * @return
     */
    //RmPendingTask getRmPendingTask();

    //void saveRmPendingTask(RmPendingTask rmPendingTask);

    /**
     * Stores TDS registration data for MDES, which is further required for fetching transaction details.
     * @param tdsRegistrationData TDS Registration Data
     */
    void saveTdsRegistrationCode(TdsRegistrationData tdsRegistrationData);

    /**
     * Returns TDS Regiistration Data.
     * @param tokenUniqueReference Token Unique Reference of the card whose transaction details needs to be fetched
     * @return TDS Registration data
     */
    TdsRegistrationData getTdsRegistrationData(String tokenUniqueReference);

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
