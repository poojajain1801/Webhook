package com.comviva.hceservice.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DebugUtils;
import android.util.Log;

import com.comviva.hceservice.LukInfo;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.CommonDatabase;
import com.comviva.hceservice.common.database.CommonDb;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.digitizationApi.ActiveAccountManagementService;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.internalSdkListeners.MdesCardManagerEventListener;
import com.comviva.hceservice.internalSdkListeners.TransactionCompletionListener;
import com.comviva.hceservice.security.DeviceStatus;
import com.comviva.hceservice.security.DexGuardSecurity;
import com.comviva.hceservice.security.SecurityInf;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mchipengine.walletinterface.walletcallbacks.WalletAdviceManager;
import com.mastercard.mchipengine.walletinterface.walletcallbacks.WalletConsentManager;
import com.mastercard.mchipengine.walletinterface.walletcommonenumeration.Advice;
import com.mastercard.mchipengine.walletinterface.walletdatatypes.AdviceAndReasons;
import com.mastercard.mchipengine.walletinterface.walletdatatypes.TerminalInformation;
import com.mastercard.mchipengine.walletinterface.walletdatatypes.TransactionInformation;
import com.mastercard.mpsdk.componentinterface.Card;
import com.mastercard.mpsdk.componentinterface.McbpLogger;
import com.mastercard.mpsdk.componentinterface.crypto.McbpCryptoServices;
import com.mastercard.mpsdk.componentinterface.crypto.WalletDataCrypto;
import com.mastercard.mpsdk.componentinterface.crypto.WalletIdentificationDataProvider;
import com.mastercard.mpsdk.componentinterface.crypto.keys.WalletDekEncryptedData;
import com.mastercard.mpsdk.componentinterface.database.state.CardState;
import com.mastercard.mpsdk.componentinterface.http.HttpManager;
import com.mastercard.mpsdk.httpmanager.MpSdkHttpManager;
import com.mastercard.mpsdk.implementation.MasterCardMobilePaymentLibrary;
import com.mastercard.mpsdk.implementation.MasterCardMobilePaymentLibraryInitializer;
import com.mastercard.mpsdk.interfaces.CdCvmStatusProvider;
import com.mastercard.mpsdk.interfaces.KeyRolloverEventListener;
import com.mastercard.mpsdk.interfaces.Mcbp;
import com.mastercard.mpsdk.interfaces.McbpInitializer;
import com.mastercard.mpsdk.mcbp.androidcrypto.McbpCryptoEngineFactory;
import com.mastercard.mpsdk.utils.Utils;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Entry Class for Comviva SDK.
 */
public class ComvivaSdk {

    private static ComvivaSdk comvivaSdk;
    private CommonDb commonDb;
    private SecurityInf securityInf;
    private static SDKData sdkData;


    private ComvivaSdk(Application application) {

        sdkData.setContext((application.getApplicationContext()));
        securityInf = DexGuardSecurity.getInstance(sdkData.getContext());
        commonDb = new CommonDatabase(sdkData.getContext());
        VisaPaymentSDKImpl.initialize(sdkData.getContext());
        // MasterCard
        Locale current = Locale.getDefault();
        if (current != Locale.ENGLISH) {
            Locale.setDefault(Locale.ENGLISH);
        }
        MasterCardMobilePaymentLibrary mobilePaymentLibrary = MasterCardMobilePaymentLibraryInitializer.INSTANCE.forApplication(application).initialize();
        com.mastercard.mpsdk.interfaces.McbpInitializer mcbpInitializer = mobilePaymentLibrary.getMcbpInitializer();
        loadConfiguration();
        initializeMcbp(mcbpInitializer, application);
        if (current != Locale.ENGLISH) {
            Locale.setDefault(current);
        }
    }


    private HttpManager getHttpManager() {
        sdkData = SDKData.getInstance();
        PropertyReader propertyReader = PropertyReader.getInstance(sdkData.getContext());
        List<String> hostNames = new ArrayList<>();
        hostNames.add("ws.mastercard.com");
        hostNames.add("www.mastercard.com");
        hostNames.add("services.mastercard.com");
        hostNames.add("stl.services.mastercard.com");
        hostNames.add("ksc.services.mastercard.com");
        return new MpSdkHttpManager(hostNames, CommonUtil.getBytesFromInputStream(propertyReader.getProperty(PropertyConst.KEY_PROVISION_CERTIFICATE_NAME,PropertyConst.COMVIVA_HCE_CREDENTIALS_FILE)),
                Constants.FORCE_TLS_PROTOCOL);
    }


    /**
     * Method to initialize MCBP Object
     */
    private void initializeMcbp(McbpInitializer mcbpInitializer, Application application) {

        WalletConsentManager walletConsentManager = new WalletConsentManager() {
            @Override
            public boolean isConsentGiven() {

                if (null == sdkData.getCdCvm()) {
                    return false;
                }
                return sdkData.getCdCvm().isStatus();
            }
        };
        WalletAdviceManager walletAdviceManager = new WalletAdviceManager() {
            @Override
            public Advice getFinalAssessment(AdviceAndReasons adviceAndReasons, TransactionInformation transactionInformation, TerminalInformation terminalInformation) {

                if (sdkData.getSelectedCard().getTransactionCredentialsLeft() != 0) {
                    return Advice.PROCEED;
                } else {
                    return Advice.DECLINE;
                }
            }
        };
        WalletIdentificationDataProvider walletIdentificationDataProvider = new WalletIdentificationDataProvider() {
            @Override
            public byte[] getPaymentAppInstanceId() {

                return Utils.fromHexStringToByteArray(CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
            }


            @Override
            public byte[] getPaymentAppProviderId() {

                return Utils.fromHexStringToByteArray(Constants.PAYMENT_APP_PROVIDER_ID);
            }


            @Override
            public WalletDekEncryptedData getEncryptedDeviceFingerPrint() {

                WalletDataCrypto walletDataCrypto = sdkData
                        .getMcbp()
                        .getWalletSecurityServices()
                        .getWalletCryptoApi();
                return walletDataCrypto.encryptWalletData(Utils.fromHexStringToByteArray(CommonUtil.getSharedPreference(Tags.DEVICE_FINGER_PRINT.getTag(), Tags.USER_DETAILS.getTag())));
            }


            @Override
            public void onKeyRollover(WalletDekEncryptedData walletDekEncryptedData) {

            }
        };
        KeyRolloverEventListener keyRolloverEventListener = new KeyRolloverEventListener() {
            @Override
            public void onTransactionsSuspended() {

            }


            @Override
            public void onTransactionsResumed() {

            }
        };
        CdCvmStatusProvider cdCvmStatusProvider = new CdCvmStatusProvider() {
            @Override
            public boolean isCdCvmEnabled() {

                if (null == sdkData.getCdCvm()) {
                    return false;
                }
                return sdkData.getCdCvm().isStatus();
                // return false;
            }


            @Override
            public boolean isCdCvmSuccessful(Card card) {

                if (null == sdkData.getCdCvm()) {
                    return false;
                }
                return sdkData.getCdCvm().isStatus();
                // return false;
            }


            @Override
            public long getTimeOfLastSuccessfulCdCvm() {

                if (null == sdkData.getCdCvm()) {
                    return 0;
                }
                return sdkData.getCdCvm().getTimeStampOFLastSuccessfulCdcvm();
                //  return 0;
            }


            @Override
            public boolean isCdCvmBlocked() {

                return false;
            }
        };
        MdesCardManagerEventListener mdesCardManagerEventListener = new MdesCardManagerEventListener();
        McbpCryptoServices mcbpCryptoServices = new McbpCryptoEngineFactory().getCryptoEngine(application);
        Mcbp mcbp = mcbpInitializer
                .usingOptionalAdviceManager(walletAdviceManager)
                .withWalletConsentManager(walletConsentManager)
                .withCdCvmStatusProvider(cdCvmStatusProvider)
                .withActiveCardProvider(sdkData.getCardSelectionManagerForTransaction())
                .withCardManagerEventListener(mdesCardManagerEventListener)
                .withCryptoEngine(mcbpCryptoServices)
                .withWalletIdentificationDataProvider(walletIdentificationDataProvider)
                .withHttpManager(getHttpManager())
                .withKeyRolloverEventListener(keyRolloverEventListener)
                .withTransactionEventListener(sdkData.getTransactionCompletionListener())
                .initialize();
        sdkData.setMcbp(mcbp);
    }


    /**
     * If device is rooted or tampered we need to clear all data and report to server.
     */
    public static void reportFraud() {

        comvivaSdk.resetDevice();
    }


    public static void checkSecurity() throws SdkException {
        // Check for Debug Mode
        SecurityInf securityInf = comvivaSdk.getSecurityInf();
        if (securityInf.isDebuggable()) {
            // Close the application
            comvivaSdk = null;
            throw new SdkException(SdkErrorStandardImpl.COMMON_DEBUG_MODE);
        }
        // Check that device is Rooted
        if (securityInf.isDeviceRooted()) {
            // Delete all data from SDK and inform to server
            reportFraud();
            throw new SdkException(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
        }
    }


    private void loadConfiguration() {

        Context ctx = sdkData.getContext();
        PropertyReader propertyReader = PropertyReader.getInstance(ctx);
        SharedPreferences sharedPrefConf = ctx.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        if (!sharedPrefConf.contains(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_IP))) {
            String paymentAppServerIp = propertyReader.getProperty(PropertyConst.KEY_IP_PAY_APP_SERVER,PropertyConst.COMVIVA_HCE_PROPERTY_FILE);
            String paymentAppServerPort = propertyReader.getProperty(PropertyConst.KEY_PORT_PAY_APP_SERVER ,PropertyConst.COMVIVA_HCE_PROPERTY_FILE);
            SharedPreferences.Editor editor = sharedPrefConf.edit();
            editor.putString(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_IP), CommonUtil.encrypt(paymentAppServerIp));
            editor.putString(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_PORT), CommonUtil.encrypt(paymentAppServerPort));
            editor.putBoolean(Constants.KEY_MDES_TDS_REG_STATUS, false);
            editor.putString(Constants.KEY_TDS_REG_TOKEN_UNIQUE_REF, null);
            editor.putString(Constants.KEY_TDS_REG_TOKEN_UNIQUE_REF, null);
            if (null != paymentAppServerIp && paymentAppServerIp.startsWith(Tags.HTTPS.getTag())) {
                editor.putBoolean(CommonUtil.encrypt(Constants.KEY_HTTPS_ENABLED), true);
            } else {
                editor.putBoolean(CommonUtil.encrypt(Constants.KEY_HTTPS_ENABLED), false);
            }
            editor.apply();
        }
        if (null == CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag())) {
            CommonUtil.setSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), CommonUtil.generatePaymentAppInstanceId(), Tags.USER_DETAILS.getTag());
        }
        if (null == CommonUtil.getSharedPreference(Tags.DEVICE_FINGER_PRINT.getTag(), Tags.USER_DETAILS.getTag())) {
            try {
                CommonUtil.setSharedPreference(Tags.DEVICE_FINGER_PRINT.getTag(), Utils.fromByteArrayToHexString(CommonUtil.getDeviceFingerprint(CommonUtil.getDeviceInfoInJson().toString().getBytes())), Tags.USER_DETAILS.getTag());
            } catch (SdkException e) {
                Log.e(Tags.ERROR_LOG.getTag(), e.getMessage());
            }
        }
        SharedPreferences userDetailsSharedPreferences = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, Context.MODE_PRIVATE);
        sdkData.setImei(userDetailsSharedPreferences.getString(Tags.IMEI.getTag(), ""));
        UrlUtil.initialize(sharedPrefConf.getString(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_IP), null),
                sharedPrefConf.getString(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_PORT), null));
        TransactionCompletionListener transactionCompletionListener = new TransactionCompletionListener();
        CardSelectionManagerForTransaction cardSelectionManagerForTransaction = new CardSelectionManagerForTransaction();
        sdkData.setTransactionCompletionListener(transactionCompletionListener);
        sdkData.setCardSelectionManagerForTransaction(cardSelectionManagerForTransaction);
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

        sdkData = SDKData.getInstance();
        if (null == sdkData.getComvivaSdk()) {
            comvivaSdk = new ComvivaSdk(context);
            sdkData.setComvivaSdk(comvivaSdk);
            checkSecurity();
        }
        // Check security
        return sdkData.getComvivaSdk();
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
     * Returns PaymentAppInstanceId
     *
     * @return PaymentAppInstanceId
     */
    public String getPaymentAppInstanceId() {
        //return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_INSTANCE_ID, null);
        return null;
    }


    /**
     * Returns PaymentAppProviderId
     *
     * @return PaymentAppProviderId
     */
    public String getPaymentAppProviderId() {
        //return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_PROVIDER_ID, null);
        return null;
    }


    /**
     * replenish new Transaction credentials for given Token
     *
     * @param paymentCard Card which needs to be replenished.
     */
    public void replenishCard(PaymentCard paymentCard, ResponseListener listener) {

        Card card = (Card) paymentCard;
        card.replenishCredentials();
        /*try {
            McbpCardApi.replenishForCardWithId(tokenUniqueReference);
        } catch (InvalidCardStateException | AlreadyInProcessException e) {
            Log.e(Tags.ERROR_LOG.getTag(), e.getMessage());
        }*/
    }


    /**
     * Checks that given token is registered for given token or not.
     *
     * @return <code>true </code>Registered for TDS<br>
     * <code>false </code>Not registered yet for TDS
     */
    public boolean isTdsRegistered() {

        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getBoolean(Constants.KEY_MDES_TDS_REG_STATUS, false);
    }


    /**
     * Returns currently Selected Cards
     *
     * @return Selected Card
     */
    public PaymentCard getSelectedCard() {
        // return selectedCard;
        return null;
    }


    /* *//**
     * Set currently selected card
     *

     *//*
    public boolean setSelectedCard(PaymentCard paymentCard) {

        this.selectedCard = paymentCard;
        switch (selectedCard.getCardType()) {
            case MDES:
                // McbpWalletApi.setCurrentCard((McbpCard) selectedCard.getCurrentCard());
                break;
            case VTS:
                try {
                    VisaPaymentSDKImpl.getInstance().selectCard(((TokenData) paymentCard.getCurrentCard()).getTokenKey());
                } catch (TokenInvalidException | CryptoException | InvalidTokenStateException e) {
                    if (e.getCbpError().getErrorCode() == SDKErrorType.SUPER_USER_PERMISSION_DETECTED.getCode()) {
                        ComvivaSdk.reportFraud();
                    }
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }*/


    /**
     * Returns all card stored in in the SDK.
     *
     * @return List of cards
     */
    public ArrayList<PaymentCard> getAllCards() throws Exception {

        ArrayList<PaymentCard> allCards = new ArrayList<>();
        List<Card> mdesCards;
        List<TokenData> vtsCards;
        String defaultCardUniqueId = commonDb.getDefaultCardUniqueId();
        SchemeType enrollmentStatus = checkEnrolmentStatus();
        PaymentCard paymentCard;
        if (enrollmentStatus == SchemeType.ALL || enrollmentStatus == SchemeType.MASTERCARD) {
            mdesCards = sdkData.getMcbp().getCardManager().getAllCards();
            for (Card mcbpCard : mdesCards) {
                paymentCard = new PaymentCard(mcbpCard);
                if (mcbpCard.getCardId().equalsIgnoreCase(defaultCardUniqueId)) {
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
        if (tokensToBeReplenished.size() > 0) {
            Intent intent = new Intent(sdkData.getContext(), ActiveAccountManagementService.class);
            intent.putExtra(com.visa.cbp.sdk.facade.data.Constants.REPLENISH_TOKENS_KEY, tokensToBeReplenished);
            sdkData.getContext().startService(intent);
        }
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

        SharedPreferences pref = sdkData.getContext().getSharedPreferences(Tags.USER_DETAILS.getTag(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        // Clear Vts related data
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        visaPaymentSDK.deleteAllTokensLocally();
        visaPaymentSDK.reset(sdkData.getContext());
        //clear data from Master Card Sdk.
        sdkData.getMcbp().resetToUninitializedState();
        // Clear Comviva SDK data
        commonDb.resetDatabase();
        sdkData.setInstanceNull();
        Log.d("SDK Initizlized", "" + isSdkInitialized());
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
     * Example - If you have IP as 172.19.4.107 then pass paymentAppServerIp as http://172.19.4.107 or https://172.19.4.107 depending on HTTP or HTTPS.<br>
     * If you have only server url and no port number then pass your url as paymentAppServerIp and port number as -1.
     * </p>
     *
     * @param paymentAppServerIp Server IP.
     * @param port               Port Number. If you do not have port number then please use value -1.
     */
    public void setPaymentAppServerConfiguration(String paymentAppServerIp, String port) {

        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefConf.edit();
        editor.putString(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_IP), CommonUtil.encrypt(paymentAppServerIp));
        editor.putString(CommonUtil.encrypt(Constants.KEY_PAYMENT_APP_SERVER_PORT), CommonUtil.encrypt(port));
        if (paymentAppServerIp.startsWith(Tags.HTTPS.getTag())) {
            editor.putBoolean(CommonUtil.encrypt(Constants.KEY_HTTPS_ENABLED), true);
        } else {
            editor.putBoolean(CommonUtil.encrypt(Constants.KEY_HTTPS_ENABLED), false);
        }
        editor.commit();
        loadConfiguration();
    }


    /**
     * Return Payment Server IP address.
     *
     * @return IP Address
     */
    public String getPaymentAppServerIP() {

        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_PAYMENT_APP_SERVER_IP, null);
    }


    /**
     * Return Payment Server Port Number.
     *
     * @return Port Number
     */
    public String getPaymentAppServerPort() {

        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_PAYMENT_APP_SERVER_PORT, null);
    }


    /**
     * Return CMS-D Server IP address.
     *
     * @return IP Address
     */
    public String getCmsDServerIP() {

        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        return sharedPrefConf.getString(Constants.KEY_CMS_D_SERVER_IP, null);
    }


    /**
     * Return CMS-D Server Port Number.
     *
     * @return Port Number
     */
    public String getCmsDServerPort() {

        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
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


    public void deleteLukInfo(PaymentCard card) {

        commonDb.deleteLukInfo(card);
    }
}
