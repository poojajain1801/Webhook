package com.comviva.hceservice.common.database;


import com.comviva.hceservice.common.RmPendingTask;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.mdes.tds.TdsRegistrationData;

public interface CommonDb {
    /**
     * Task ID for Provision
     */
    String RM_T_ID_001 = "RM_T_ID_001";
    /**
     * Task ID for DELETE TOKEN
     */
    String RM_T_ID_002 = "RM_T_ID_002";
    /**
     * Task ID for Replenish TOKEN
     */
    String RM_T_ID_003 = "RM_T_ID_003";

    void initializeComvivaSdk(ComvivaSdkInitData comvivaSdkInitData);

    void setRnsInfo(RnsInfo rnsInfo);

    ComvivaSdkInitData getInitializationData();

    RmPendingTask getRmPendingTask();

    void saveRmPendingTask(RmPendingTask rmPendingTask);

    void saveTdsRegistrationCode(TdsRegistrationData tdsRegistrationData);

    TdsRegistrationData getTdsRegistrationData(String tokenUniqueReference);
}
