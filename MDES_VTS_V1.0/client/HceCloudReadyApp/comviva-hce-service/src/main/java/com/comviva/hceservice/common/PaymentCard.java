package com.comviva.hceservice.common;

import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.cvm.PinListener;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.init.SdkContext;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.listeners.ProcessContactlessListener;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenStatus;

/**
 * Encapsulates Card Object. Provides methods to fetch card's presentation attributes like
 * last 4 digits, current state etc.
 */
public class PaymentCard {
    private Object currentCard;
    private CardType cardType;

    PaymentCard(Object cardObj) {
        currentCard = cardObj;

        if (cardObj instanceof McbpCard) {
            cardType = CardType.MDES;
        } else if (cardObj instanceof TokenData) {
            cardType = CardType.VTS;
        } else {
            currentCard = null;
            cardType = CardType.UNKNOWN;
        }
    }

    Object getCurrentCard() {
        return currentCard;
    }

    /**
     * Get card type.
     *
     * @return card type
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Returns Card number's last 4 digit.
     * @return Last 4 digit
     */
    public String getCardLast4Digit() {
        try {
            switch (cardType) {
                case MDES:
                    McbpCard card = (McbpCard) currentCard;
                    LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
                    String tokenUniqueReference = ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(card.getDigitizedCardId());
                    SdkContext sdkContext = SdkContext.initialize(ComvivaSdk.getInstance(null).getApplicationContext());
                    return sdkContext.getLdeMcbpCardService().getDisplayablePanDigits(tokenUniqueReference);

                case VTS:
                    TokenData tokenData = (TokenData) currentCard;
                    return tokenData.getPaymentInstrumentLast4();
            }


        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Card's current state.
     * @return Card State
     */
    public CardState getCardState() {
        try {
            switch (cardType) {
                case MDES:
                    McbpCard card = (McbpCard) currentCard;
                    LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
                    ProfileState cardState = ldeRemoteManagementService.getCardState(card.getDigitizedCardId());
                    switch (cardState) {
                        case SUSPENDED:
                            return CardState.SUSPENDED;

                        case INITIALIZED:
                            return CardState.INITIALIZED;

                        case UNINITIALIZED:
                            return CardState.UNINITIALIZED;
                    }
                    break;

                case VTS:
                    TokenData tokenData = (TokenData) currentCard;
                    TokenStatus tokenStatus = TokenStatus.valueOf(tokenData.getTokenStatus());
                    switch (tokenStatus) {
                        case ACTIVE:
                            return CardState.INITIALIZED;

                        case SUSPENDED:
                            return CardState.SUSPENDED;

                        default:
                            return null;
                    }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Returns Number of transaction credential i.e. how many transactions a card can perform.
     * @return Number of transaction credentials remaining
     */
    public int getTransactionCredentialsLeft() {
        switch (cardType) {
            case MDES:
                return ((McbpCard) currentCard).numberPaymentsLeft();

            case VTS:
            default:
                return 1;
        }
    }

    /**
     * CVM reset timeout in seconds.
     * @return CVM Reset timeout
     */
    public int getCvmResetTimeout() {
        switch (cardType) {
            case MDES:
                return ((McbpCard) currentCard).getCvmResetTimeOut();

            case VTS:
            default:
                return 30;
        }
    }

    /**
     *Card's Unique Identification Number.
     * @return Unique Id
     */
    public String getCardUniqueId() {
        try {
            switch (cardType) {
                case MDES:
                    McbpCard card = (McbpCard) currentCard;
                    LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
                    return ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(card.getDigitizedCardId());

                case VTS:
                    return Long.toString(((TokenData) currentCard).getTokenKey().getTokenId());
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Prepares card for contactless transaction.
     * @param processContactlessListener Listener to transaction events.
     */
    public void prepareForContactlessTransaction(ProcessContactlessListener processContactlessListener){
        if(cardType == CardType.MDES) {
            McbpCardApi.prepareContactless((McbpCard) currentCard, processContactlessListener);
        }
    }

    public void startContactlessTransaction() {
        try {
            if (cardType == CardType.MDES) {
                McbpCard card = (McbpCard) currentCard;
                card.startContactless(new BusinessLogicTransactionInformation());
            }
        } catch (Exception e) {
        }
    }

    /**
     * Terminates contactless transaction.
     */
    public void stopContactlessTransaction() {
        try {
            if (cardType == CardType.MDES) {
                McbpCard card = (McbpCard) currentCard;
                card.stopContactLess();
            }
        } catch (Exception e) {
        }
    }

    /**
     * Invoke this method just after user enters PIN.
     * @param pinListener PIN Listener
     * @param pin PIN Value
     */
    public void pinEntered(PinListener pinListener, final String pin) {
        pinListener.pinEntered(ByteArray.of(pin.getBytes()));
    }
}
