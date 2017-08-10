package com.comviva.hceservice.common;

import android.app.Application;
import android.content.Context;

import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.CommonDatabase;
import com.comviva.hceservice.common.database.CommonDb;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;

/**
 * Entry Class for Comviva SDK.
 */
public class ComvivaHce {
    private static ComvivaHce comvivaHce;
    private CommonDb commonDb;
    private Application application;

    private PaymentCard paymentCard;

    private ComvivaHce(Application application) {
        this.application = application;
        paymentCard = new PaymentCard();
        commonDb = new CommonDatabase(application.getApplicationContext());
        VisaPaymentSDKImpl.initialize(application.getApplicationContext());
        McbpInitializer.setup(application, null);
    }

    /**
     * Returns Singleton Instance of this class.
     * @param context Current Context
     * @return Singleton ComvivaHce Instance
     */
    public static ComvivaHce getInstance(Application context) {
        if(comvivaHce == null) {
            PropertyReader propertyReader = PropertyReader.getInstance(context);
            UrlUtil.initialize(propertyReader.getProperty(PropertyConst.KEY_IP_PAY_APP_SERVER),
                    propertyReader.getProperty(PropertyConst.KEY_PORT_PAY_APP_SERVER),
                    propertyReader.getProperty(PropertyConst.KEY_IP_CMS_D),
                    propertyReader.getProperty(PropertyConst.KEY_PORT_CMS_D));
            comvivaHce = new ComvivaHce(context);
        }
        return comvivaHce;
    }

    /**
     * Checks that SDK is initialized of not.
     *
     * @return <code>true </code> SDK is initialized <br>
     *     <code>false </code> SDK is uninitialized
     */
    public boolean isSdkInitialized() {
        return commonDb.getInitializationData().isInitState();
    }

    /**
     * Returns Remote Notification Detail.
     * @return RnsInfo object
     */
    public RnsInfo getRnsInfo() {
        return commonDb.getInitializationData().getRnsInfo();
    }

    /**
     * Saves Remote Notification Detail
     * @param rnsInfo
     */
    public void saveRnsInfo(RnsInfo rnsInfo) {
        commonDb.setRnsInfo(rnsInfo);
    }

    public void initializeSdk(ComvivaSdkInitData initData) {
        commonDb.initializeComvivaSdk(initData);
    }

    public ComvivaSdkInitData getInitializationData() {
        return commonDb.getInitializationData();
    }

    public Context getApplicationContext() {
        return application;
    }

    public String getPaymentAppInstanceId() {
        return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_INSTANCE_ID, null);
    }

    public String getPaymentAppProviderId() {
        return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_PROVIDER_ID, null);
    }

    public CommonDb getCommonDb() {
        return commonDb;
    }

    /**
     * replenish new Transaction credentials for given Token
     * @param tokenUniqueReference  TokenUniqueReference which needs to be replenished.
     */
    public void replenishCard(String tokenUniqueReference) {
        try {
            McbpCardApi.replenishForCardWithId(tokenUniqueReference);
        } catch (InvalidCardStateException | AlreadyInProcessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks that given token is registered for given token or not.
     * @param tokenUniqueReference Token Unique Reference to checked.
     * @return <code>true </code>Registered for TDS<br>
     *     <code>false </code>Not registered yet for TDS
     */
    public boolean isTdsRegistered(final String tokenUniqueReference) {
        return commonDb.getTdsRegistrationData(tokenUniqueReference) != null;
    }

    public PaymentCard getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(Object paymentCard) {
        this.paymentCard.setCurrentCard(paymentCard);
    }
}
