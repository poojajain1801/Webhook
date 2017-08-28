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
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
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

    private PaymentCard selectedCard;

    private ComvivaSdk(Application application) {
        this.application = application;
        selectedCard = null;
        commonDb = new CommonDatabase(application.getApplicationContext());
        VisaPaymentSDKImpl.initialize(application.getApplicationContext());
        McbpInitializer.setup(application, null);
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
        List<McbpCard> mdesCards = McbpWalletApi.getCards(true);
        List<TokenData> vtsCards = VisaPaymentSDKImpl.getInstance().getAllTokenData();

        for (McbpCard mcbpCard : mdesCards) {
            allCards.add(new PaymentCard(mcbpCard));
        }

        for (TokenData tokenData :  vtsCards) {
            allCards.add(new PaymentCard(tokenData));
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
     * Return initialization state of SDK with VTS System.
     * @return <code>true </code>Device registered successfully with VTS. <br>
     *         <code>false </code>Device not registered with VTS. <br>
     */
    public boolean isVtsInitialized() {
        return commonDb.getInitializationData().isVtsInitState();
    }

    /**
     * Return initialization state of SDK with MDES System.
     * @return <code>true </code>Device registered successfully with MDES. <br>
     *         <code>false </code>Device not registered with MDES. <br>
     */
    public boolean isMdesInitialized() {
        return commonDb.getInitializationData().isMdesInitState();
    }
}
