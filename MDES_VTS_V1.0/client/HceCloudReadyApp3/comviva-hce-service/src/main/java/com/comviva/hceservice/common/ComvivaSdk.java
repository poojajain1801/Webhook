package com.comviva.hceservice.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.comviva.hceservice.LukInfo;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.CommonDatabase;
import com.comviva.hceservice.common.database.CommonDb;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.digitizationApi.ActiveAccountManagementService;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.register.Registration;
import com.comviva.hceservice.security.DexGuardSecurity;
import com.comviva.hceservice.security.SecurityInf;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.error.SDKErrorType;
import com.visa.cbp.sdk.facade.exception.CryptoException;
import com.visa.cbp.sdk.facade.exception.InvalidTokenStateException;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry Class for Comviva SDK.
 */
public class ComvivaSdk {
    private static ComvivaSdk comvivaSdk;
    private CommonDb commonDb;
    private Application application;
    private SecurityInf securityInf;

    private PaymentCard selectedCard;

    private ComvivaSdk(Application application) {
        this.application = application;
        selectedCard = null;
        securityInf = DexGuardSecurity.getInstance(application.getApplicationContext());
        commonDb = new CommonDatabase(application.getApplicationContext());
        VisaPaymentSDKImpl.initialize(application.getApplicationContext());
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        visaPaymentSDK.getDeviceInfo("");
        McbpInitializer.setup(application, null);
        loadConfiguration();
    }

    /**
     * If device is rooted or tampered we need to clear all data and report to server.
     */
    public static void reportFraud() {

        comvivaSdk.resetDevice();
        comvivaSdk = null;
    }

    public  static void checkSecurity() throws SdkException {
        // Check for Debug Mode
      /*  SecurityInf securityInf = comvivaSdk.getSecurityInf();
        if (securityInf.isDebuggable()) {
            // Close the application
            Log.d("Security","Debug not allowed");
            comvivaSdk = null;
            throw new SdkException(SdkErrorStandardImpl.COMMON_DEBUG_MODE);
        }

        // Check that device is Rooted
        if (securityInf.isDeviceRooted()) {
            // Delete all data from SDK and inform to server
            Log.d("Security","Device is rooted");
            reportFraud();
            throw new SdkException(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
        }*/


    }

    private void loadConfiguration() {
        Context ctx = application.getApplicationContext();
        PropertyReader propertyReader = PropertyReader.getInstance(ctx);
        SharedPreferences sharedPrefConf = ctx.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        if (!sharedPrefConf.contains(Constants.KEY_PAYMENT_APP_SERVER_IP)) {
            String paymentAppServerIp = propertyReader.getProperty(PropertyConst.KEY_IP_PAY_APP_SERVER);
            String paymentAppServerPort = propertyReader.getProperty(PropertyConst.KEY_PORT_PAY_APP_SERVER);
            String cmsdServerIp = propertyReader.getProperty(PropertyConst.KEY_IP_CMS_D);
            String cmsdServerPort = propertyReader.getProperty(PropertyConst.KEY_PORT_CMS_D);

            SharedPreferences.Editor editor = sharedPrefConf.edit();
            editor.putString(Constants.KEY_PAYMENT_APP_SERVER_IP, paymentAppServerIp);
            editor.putString(Constants.KEY_PAYMENT_APP_SERVER_PORT, paymentAppServerPort);
            editor.putString(Constants.KEY_CMS_D_SERVER_IP, cmsdServerIp);
            editor.putString(Constants.KEY_CMS_D_SERVER_PORT, cmsdServerPort);
            editor.putBoolean(Constants.KEY_MDES_TDS_REG_STATUS, false);
            editor.putString(Constants.KEY_TDS_REG_TOKEN_UNIQUE_REF, null);
            editor.apply();
        }

        UrlUtil.initialize(sharedPrefConf.getString(Constants.KEY_PAYMENT_APP_SERVER_IP, null),
                sharedPrefConf.getString(Constants.KEY_PAYMENT_APP_SERVER_PORT, null),
                sharedPrefConf.getString(Constants.KEY_CMS_D_SERVER_IP, null),
                sharedPrefConf.getString(Constants.KEY_CMS_D_SERVER_PORT, null));
    }

    LukInfo getLukInfo(PaymentCard card) {
        return commonDb.getLukInfoVisa(card.getCardUniqueId());
    }

    /**
     * <p>Returns Singleton Instance of this class.</p>
     * <p>Note-Invoke this method for at-least once while starting the application to initialize ComvivaSdk object</p>
     *
     * @param context Current Context
     * @return Singleton ComvivaSdk Instance
     * @throws SdkException If debug mode is on, device is rooted or apk is tampered
     */
    public static ComvivaSdk getInstance(Application context) throws SdkException {
        if (comvivaSdk == null) {
            comvivaSdk = new ComvivaSdk(context);
            checkSecurity();
        }

        // Check security

        return comvivaSdk;
    }

    /**
     * Checks that SDK is initialized of not.
     *
     * @return <code>true </code> SDK is initialized <br>
     * <code>false </code> SDK is uninitialized
     */
    public boolean isSdkInitialized() {
        return commonDb.getInitializationData().isInitState();
    }

    /**
     * Returns Remote Notification Detail.
     *
     * @return RnsInfo object
     */
    public RnsInfo getRnsInfo() {
        return commonDb.getInitializationData().getRnsInfo();
    }

    /**
     * Saves Remote Notification Detail
     *
     * @param rnsInfo
     */
    public void saveRnsInfo(RnsInfo rnsInfo) {
        commonDb.setRnsInfo(rnsInfo);
    }

    /**
     * Initialize ComvivaSdk with initial data.
     *
     * @param initData Initialization data
     */
    public void initializeSdk(ComvivaSdkInitData initData) {
        commonDb.initializeComvivaSdk(initData);
    }

    /**
     * Returns Initialization Data.
     *
     * @return Initialization data
     */
    public ComvivaSdkInitData getInitializationData() {
        return commonDb.getInitializationData();
    }

    /**
     * Returns Application context
     *
     * @return Application Context
     */
    public Context getApplicationContext() {
        return application;
    }

    /**
     * Returns PaymentAppInstanceId
     *
     * @return PaymentAppInstanceId
     */
    public String getPaymentAppInstanceId() {
        return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_INSTANCE_ID, null);
    }

    /**
     * Returns PaymentAppProviderId
     *
     * @return PaymentAppProviderId
     */
    public String getPaymentAppProviderId() {
        return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_PROVIDER_ID, null);
    }

    /**
     * replenish new Transaction credentials for given Token
     *
     * @param tokenUniqueReference TokenUniqueReference which needs to be replenished.
     */
    public void replenishCard(String tokenUniqueReference) {
        try {
            McbpCardApi.replenishForCardWithId(tokenUniqueReference);
        } catch (InvalidCardStateException | AlreadyInProcessException e) {
            Log.d("Error",e.getMessage());
        }
    }

    /**
     * Checks that given token is registered for given token or not.
     *
     * @return <code>true </code>Registered for TDS<br>
     * <code>false </code>Not registered yet for TDS
     */
    public boolean isTdsRegistered() {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getBoolean(Constants.KEY_MDES_TDS_REG_STATUS, false);
    }

    /**
     * Returns currently Selected Cards
     *
     * @return Selected Card
     */
    public PaymentCard getSelectedCard() {
        return selectedCard;
    }

    /**
     * Set currently selected card
     *
     * @param paymentCard Card to be selected
     */
    public boolean setSelectedCard(PaymentCard paymentCard) {
        this.selectedCard = paymentCard;
        switch (selectedCard.getCardType()) {
            case MDES:
                McbpWalletApi.setCurrentCard((McbpCard) selectedCard.getCurrentCard());
                break;

            case VTS:
                try {
                    VisaPaymentSDKImpl.getInstance().selectCard(((TokenData) paymentCard.getCurrentCard()).getTokenKey());
                } catch (TokenInvalidException | CryptoException | InvalidTokenStateException e) {
                    if (e.getCbpError().getErrorCode() == SDKErrorType.SUPER_USER_PERMISSION_DETECTED.getCode())
                    {
                        ComvivaSdk.reportFraud();
                    }
                    return false;
                }
                break;

                default:
                    break;
        }
        return true;
    }
    public void deSelectCard ()
    {
        VisaPaymentSDKImpl.getInstance().deselectCard();
        this.selectedCard = null;
    }


    /**
     * Returns all card stored in in the SDK.
     *
     * @return List of cards
     */
    public ArrayList<PaymentCard> getAllCards() {
        ArrayList<PaymentCard> allCards = new ArrayList<>();
        List<McbpCard> mdesCards;
        List<TokenData> vtsCards;

        String defaultCardUniqueId = commonDb.getDefaultCardUniqueId();
        SchemeType enrollmentStatus = checkEnrolmentStatus();
        PaymentCard paymentCard;

        if (enrollmentStatus == SchemeType.ALL || enrollmentStatus == SchemeType.MASTERCARD) {
            mdesCards = McbpWalletApi.getCards(true);
            for (McbpCard mcbpCard : mdesCards) {
                paymentCard = new PaymentCard(mcbpCard);
                if (mcbpCard.getDigitizedCardId().equalsIgnoreCase(defaultCardUniqueId)) {
                    paymentCard.setDefaultCard();
                }
                allCards.add(paymentCard);
            }
        }

        if (enrollmentStatus == SchemeType.ALL || enrollmentStatus == SchemeType.VISA) {
            vtsCards = VisaPaymentSDKImpl.getInstance().getAllTokenData();
            for (TokenData tokenData : vtsCards) {
                paymentCard = new PaymentCard(tokenData);
                if (paymentCard.getCardUniqueId().equalsIgnoreCase(defaultCardUniqueId)) {
                    paymentCard.setDefaultCard();
                }
                allCards.add(paymentCard);
            }

        }
        return allCards;
    }
public void replenishLUKVisa() {
    List<TokenData> vtsCards;
    ArrayList<TokenKey> tokensToBeReplenished = new ArrayList<>();
    PaymentCard paymentCard;
    vtsCards = VisaPaymentSDKImpl.getInstance().getAllTokenData();
    for (TokenData tokenData : vtsCards) {
        paymentCard = new PaymentCard(tokenData);

        // Check if current token has reached it's thresold limit or Key is Expired, then replenishment is required
        LukInfo lukInfo = comvivaSdk.getLukInfo(paymentCard);
        if (lukInfo != null) {
            boolean isReplenishmentRequired = (lukInfo.getNoOfPaymentsRemaining() <= 0);

            if (isReplenishmentRequired) {
                tokensToBeReplenished.add(tokenData.getTokenKey());
            }
        }

    }
    if(tokensToBeReplenished.size() > 0) {
        Intent intent = new Intent(application.getApplicationContext(), ActiveAccountManagementService.class);
        intent.putExtra(com.visa.cbp.sdk.facade.data.Constants.REPLENISH_TOKENS_KEY, tokensToBeReplenished);
        application.getApplicationContext().startService(intent);
    }
}

    /**
     * Activates a card recently added.<br>
     * Invoke this method within public boolean onCardAdded(final String tokenUniqueReference) method of ComvivaWalletListener.
     *
     * @param tokenUniqueReference TokenUniqueReference received in onCardAdded method as parameter..
     * @return <code>true </code>If card is activated successfully<br>
     * <code>false </code>Card is not activated
     */
    public boolean activateCard(final String tokenUniqueReference) {
        try {
            String digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService().getCardIdFromTokenUniqueReference(tokenUniqueReference);

            LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
            ProfileState cardState = ldeRemoteManagementService.getCardState(digitizedCardId);

            if (!cardState.equals(ProfileState.INITIALIZED)) {
                return McbpCardApi.activateCard(ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(digitizedCardId));
            }
            return true;
        } catch (InvalidInput invalidInput) {
            Log.d("Error", invalidInput.getMessage());
        }
        return false;
    }

    /**
     * Checks Enrollment status of device with all supported scheme.
     *
     * @return Enrollment status
     */
    public SchemeType checkEnrolmentStatus() {
        ComvivaSdkInitData initData = commonDb.getInitializationData();
        boolean isVtsInitialized = initData.isVtsInitialized();
        boolean isMdesInitialized = initData.isMdesInitialized();

        if (isMdesInitialized && isVtsInitialized) {
            return SchemeType.ALL;
        }

        if (isVtsInitialized) {
            return SchemeType.VISA;
        }

        if (isMdesInitialized) {
            return SchemeType.MASTERCARD;
        }
        return SchemeType.NONE;
    }

    /**
     * Clears all data andd reset device to un-initialized state.
     *
     * @return <code>true </code>Reset device successful <br>
     * <code>false </code>Reset device failed
     */
    public boolean resetDevice() {
        // Clear MDES related data.
        McbpInitializer.getInstance().getLdeRemoteManagementService().unregister();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Registration.user_details,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        // Clear VTS related data
        try {
            comvivaSdk = ComvivaSdk.getInstance(application);
        } catch (SdkException e) {
            e.printStackTrace();
        }
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        visaPaymentSDK.deleteAllTokensLocally();
        visaPaymentSDK.reset(comvivaSdk.getApplicationContext());
        // Clear Comviva SDK data
        commonDb.resetDatabase();
        comvivaSdk = null;
        Registration.setInstance(null);
        Log.d("SDK Initizlized" , "" +  isSdkInitialized());

        return true;
    }

    /**
     * Set default card.
     *
     * @param paymentCard Payment Card to be set as default card
     */
    public void setDefaultCard(PaymentCard paymentCard) {
        commonDb.setDefaultCard(paymentCard);
    }

    /**
     * Returns default card set.
     *
     * @return Default Card
     */
    public PaymentCard getDefaultPaymentCard() {
        return commonDb.getDefaultCard();
    }

    /**
     * Returns SecurityInf instance.
     *
     * @return SecurityInf Instance
     */
    public SecurityInf getSecurityInf() {
        return securityInf;
    }

    /**
     * Set SecurityInf instance.
     *
     * @param securityInf SecurityInf Instance
     */
    public void setSecurityInf(SecurityInf securityInf) {
        this.securityInf = securityInf;
    }

    /**
     * Update Payment App Server IP & Port Number. <br>
     * <p>
     *     Example - If you have IP as 172.19.4.107 then pass paymentAppServerIp as http://172.19.4.107 or https://172.19.4.107 depending on HTTP or HTTPS.<br>
     *         If you have only server url and no port number then pass your url as paymentAppServerIp and port number as -1.
     * </p>
     *
     * @param paymentAppServerIp Server IP.
     * @param port               Port Number. If you do not have port number then please use value -1.
     */
    public void setPaymentAppServerConfiguration(String paymentAppServerIp, int port) {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefConf.edit();
        editor.putString(Constants.KEY_PAYMENT_APP_SERVER_IP, paymentAppServerIp);
        editor.putString(Constants.KEY_PAYMENT_APP_SERVER_PORT, String.format("%d", port));
        editor.commit();
        loadConfiguration();
    }

    /**
     * Update CMS-D Server IP & Port Number.
     *
     * @param cmsDServerIp Server IP
     * @param port         Port Number
     */
    public void setCmsDServerConfiguration(String cmsDServerIp, int port) {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefConf.edit();
        editor.putString(Constants.KEY_CMS_D_SERVER_IP, cmsDServerIp);
        editor.putString(Constants.KEY_CMS_D_SERVER_PORT, String.format("%d", port));
        editor.commit();
    }

    /**
     * Return Payment Server IP address.
     *
     * @return IP Address
     */
    public String getPaymentAppServerIP() {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_PAYMENT_APP_SERVER_IP, null);
    }

    /**
     * Return Payment Server Port Number.
     *
     * @return Port Number
     */
    public String getPaymentAppServerPort() {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_PAYMENT_APP_SERVER_PORT, null);
    }

    /**
     * Return CMS-D Server IP address.
     *
     * @return IP Address
     */
    public String getCmsDServerIP() {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_CMS_D_SERVER_IP, null);
    }

    /**
     * Return CMS-D Server Port Number.
     *
     * @return Port Number
     */
    public String getCmsDServerPort() {
        SharedPreferences sharedPrefConf = application.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_CMS_D_SERVER_PORT, null);
    }

    /**
     * Reset default card. There will be no card set as default card.
     */
    public void resetDefaultCard() {
        commonDb.resetDefaultCard();
    }

    /**
     * Returns Default Card's Unique ID.
     *
     * @return Card Unique Id
     */
    public String getDefaultCardUniqueId() {
        return commonDb.getDefaultCardUniqueId();
    }

    public boolean consumeLuk(PaymentCard card) {
        return commonDb.consumeLuk(card);
    }

    public boolean insertLukInfo(LukInfo lukInfo) {
        return commonDb.insertLukInfo(lukInfo);
    }

    public boolean updateLukInfo(LukInfo lukInfo) {
        return commonDb.updateLukInfo(lukInfo);
    }

    public  void deleteLukInfo(PaymentCard card) {
        commonDb.deleteLukInfo(card);
    }
}
