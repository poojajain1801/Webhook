package com.comviva.hceservice.common;

import android.app.Application;
import android.content.Context;

import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.CommonDatabase;
import com.comviva.hceservice.common.database.CommonDb;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.security.DexGuardSecurity;
import com.comviva.hceservice.security.SecurityInf;
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
        McbpInitializer.setup(application, null);
    }

    /**
     * If device is rooted or tampered we need to clear all data and report to server.
     */
    private static void reportFraud() {
        comvivaSdk = null;
        comvivaSdk.resetDevice();

        // TODO Report to Server
    }

    /**
     * Returns Singleton Instance of this class.
     * @param context Current Context
     * @return Singleton ComvivaSdk Instance
     */
    public static ComvivaSdk getInstance(Application context) {
        if(comvivaSdk == null) {
            PropertyReader propertyReader = PropertyReader.getInstance(context);
            UrlUtil.initialize(propertyReader.getProperty(PropertyConst.KEY_IP_PAY_APP_SERVER),
                    propertyReader.getProperty(PropertyConst.KEY_PORT_PAY_APP_SERVER),
                    propertyReader.getProperty(PropertyConst.KEY_IP_CMS_D),
                    propertyReader.getProperty(PropertyConst.KEY_PORT_CMS_D));
            comvivaSdk = new ComvivaSdk(context);
        }

        // Check for Debug Mode
        SecurityInf securityInf = comvivaSdk.getSecurityInf();
//        if(securityInf.isDebuggable()) {
//            // Close the application
//            comvivaSdk = null;
//            //throw new SdkException(SdkErrorStandardImpl.SW_COMMON_DEBUG_MODE);
//        }

        // Check that device is Rooted
//        if(securityInf.isDeviceRooted()) {
//            // Delete all data from SDK and inform to server
//            reportFraud();
//            //throw new SdkException(SdkErrorStandardImpl.SW_COMMON_DEBUG_MODE);
//        }

        // Check for Tamper detection
//        if(securityInf.isApkTampered()) {
//            // Delete all data from SDK and inform to server
//            reportFraud();
//            //throw new SdkException(SdkErrorStandardImpl.SW_COMMON_DEBUG_MODE);
//        }
        return comvivaSdk;
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

    /**
     * Initialize ComvivaSdk with initial data.
     * @param initData Initialization data
     */
    public void initializeSdk(ComvivaSdkInitData initData) {
        commonDb.initializeComvivaSdk(initData);
    }

    /**
     * Returns Initialization Data.
     * @return Initialization data
     */
    public ComvivaSdkInitData getInitializationData() {
        return commonDb.getInitializationData();
    }

    /**
     * Returns Application context
     * @return Application Context
     */
    public Context getApplicationContext() {
        return application;
    }

    /**
     * Returns PaymentAppInstanceId
     * @return PaymentAppInstanceId
     */
    public String getPaymentAppInstanceId() {
        return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_INSTANCE_ID, null);
    }

    /**
     * Returns PaymentAppProviderId
     * @return PaymentAppProviderId
     */
    public String getPaymentAppProviderId() {
        return McbpInitializer.getInstance().getProperty(McbpInitializer.PAYMENT_APP_PROVIDER_ID, null);
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

    /**
     * Returns currently Selected Cards
     * @return Selected Card
     */
    public PaymentCard getSelectedCard() {
        return selectedCard;
    }

    /**
     * Set currently selected card
     * @param paymentCard Card to be selected
     */
    public void setSelectedCard(PaymentCard paymentCard) {
        this.selectedCard = paymentCard;
        if(selectedCard.getCardType() == CardType.MDES) {
            McbpWalletApi.setCurrentCard((McbpCard) selectedCard.getCurrentCard());
        }
    }

    /**
     * Returns all card stored in in the SDK.
     * @return List of cards
     */
    public ArrayList<PaymentCard> getAllCards() {
        ArrayList<PaymentCard> allCards = new ArrayList<>();
        List<McbpCard> mdesCards;
        List<TokenData> vtsCards;

        //String defaultCardUniqueId = commonDb.getDefaultCardUniqueId();
        String defaultCardUniqueId = "";
        SchemeType enrollmentStatus = checkEnrolmentStatus();
        PaymentCard paymentCard;

        if(enrollmentStatus == SchemeType.ALL || enrollmentStatus == SchemeType.MASTERCARD) {
            mdesCards = McbpWalletApi.getCards(true);
            for (McbpCard mcbpCard : mdesCards) {
                paymentCard = new PaymentCard(mcbpCard);
                if(mcbpCard.getDigitizedCardId().equalsIgnoreCase(defaultCardUniqueId)) {
                    paymentCard.setDefaultCard();
                }
                allCards.add(paymentCard);
            }
        }

        if(enrollmentStatus == SchemeType.ALL || enrollmentStatus == SchemeType.VISA) {
            vtsCards = VisaPaymentSDKImpl.getInstance().getAllTokenData();
            for (TokenData tokenData :  vtsCards) {
                paymentCard = new PaymentCard(tokenData);
                if(tokenData.getTokenKey().getTokenId() == Long.parseLong(defaultCardUniqueId)) {
                    paymentCard.setDefaultCard();
                }
                allCards.add(paymentCard);
            }
        }
        return allCards;
    }

    /**
     * Activates a card recently added.<br>
     * Invoke this method within public boolean onCardAdded(final String tokenUniqueReference) method of ComvivaWalletListener.
     * @param tokenUniqueReference TokenUniqueReference received in onCardAdded method as parameter..
     * @return <code>true </code>If card is activated successfully<br>
     *     <code>false </code>Card is not activated
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
            invalidInput.printStackTrace();
        }
        return false;
    }

    /**
     * Checks Enrollment status of device with all supported scheme.
     * @return Enrollment status
     */
    public SchemeType checkEnrolmentStatus() {
        ComvivaSdkInitData initData = commonDb.getInitializationData();
        boolean isVtsInitialized = initData.isVtsInitialized();
        boolean isMdesInitialized = initData.isMdesInitialized();

        if(isMdesInitialized && isVtsInitialized) {
            return SchemeType.ALL;
        }

        if(isVtsInitialized) {
            return SchemeType.VISA;
        }

        if(isMdesInitialized) {
            return SchemeType.MASTERCARD;
        }
        return SchemeType.NONE;
    }

    /**
     * Clears all data andd reset device to un-initialized state.
     * @return <code>true </code>Reset device successful <br>
     *     <code>false </code>Reset device failed
     */
    public boolean resetDevice() {
        // Clear MDES related data.
        McbpInitializer.getInstance().getLdeRemoteManagementService().unregister();

        // Clear VTS related data
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        visaPaymentSDK.deleteAllTokensLocally();

        // Clear Comviva SDK data
        commonDb.resetDatabase();
        return true;
    }

    /**
     * Set default card.
     * @param paymentCard Payment Card to be set as default card
     */
    public void setDefaultCard(PaymentCard paymentCard) {
        commonDb.setDefaultCard(paymentCard);
    }

    /**
     * Returns default card set.
     * @return Default Card
     */
    public PaymentCard getDefauPaymentCard() {
        return commonDb.getDefaultCard();
    }

    /**
     * Returns SecurityInf instance.
     * @return SecurityInf Instance
     */
    public SecurityInf getSecurityInf() {
        return securityInf;
    }

    /**
     * Set SecurityInf instance.
     * @param securityInf SecurityInf Instance
     */
    public void setSecurityInf(SecurityInf securityInf) {
        this.securityInf = securityInf;
    }
}
