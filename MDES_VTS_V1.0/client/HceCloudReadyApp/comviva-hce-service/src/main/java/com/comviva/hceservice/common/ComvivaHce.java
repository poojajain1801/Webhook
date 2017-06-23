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
import com.mastercard.mcbp.api.McbpApi;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.MpaManagementApi;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;

import java.util.ArrayList;

/**
 * Global object.
 * Created by tarkeshwar.v on 3/11/2017.
 */
public class ComvivaHce {
    private static ComvivaHce comvivaHce;
    private CommonDb commonDb;
    private Application application;

    private ComvivaHce(Application application) {
        this.application = application;
        commonDb = new CommonDatabase(application.getApplicationContext());
        VisaPaymentSDKImpl.initialize(application.getApplicationContext());
        McbpInitializer.setup(application, null);
    }

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

    public boolean isSdkInitialized() {
        return commonDb.getInitializationData().isInitState();
    }

    public RnsInfo getRnsInfo() {
        return commonDb.getInitializationData().getRnsInfo();
    }

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

    public void replenishCard(String tokenUniqueReference) {
        try {
            McbpCardApi.replenishForCardWithId(tokenUniqueReference);
            McbpCardApi.suspendCard(tokenUniqueReference);
        } catch (InvalidCardStateException | AlreadyInProcessException e) {
            e.printStackTrace();
        }
    }
}
