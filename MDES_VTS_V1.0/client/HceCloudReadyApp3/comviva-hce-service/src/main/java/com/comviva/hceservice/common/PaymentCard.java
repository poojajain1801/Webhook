package com.comviva.hceservice.common;

import android.util.Log;

import com.comviva.hceservice.LukInfo;
import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.mastercard.mpsdk.componentinterface.Card;
import com.mastercard.mpsdk.componentinterface.RolloverInProgressException;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenStatus;
import com.visa.cbp.sdk.facade.exception.RootDetectException;

/**
 * Encapsulates Card Object. Provides methods to fetch card's presentation attributes like
 * last 4 digits, current state etc.
 */
public class PaymentCard {

    private Object currentCard;
    private CardType cardType;
    private boolean isDefaultCard;
    private CdCvm cdCvm;
    private SDKData sdkData;


    PaymentCard(Object cardObj) {

        sdkData = SDKData.getInstance();
        currentCard = cardObj;
        if (cardObj instanceof Card) {
            cardType = CardType.MDES;
        } else if (cardObj instanceof TokenData) {
            cardType = CardType.VTS;
        } else {
            currentCard = null;
            cardType = CardType.UNKNOWN;
        }
    }


    /**
     * Set flag as current card as default card.s
     */
    void setDefaultCard() {

        isDefaultCard = true;
    }


    public static PaymentCard getPaymentCard(Object cardObj) {

        return new PaymentCard(cardObj);
    }


    public Object getCurrentCard() {

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
     *
     * @return Last 4 digit
     */
    public String getCardLast4Digit() {

        try {
            switch (cardType) {
                case MDES:
                    Card card = (Card) currentCard;
                    return card.getDisplayablePanDigits();
                case VTS:
                    TokenData tokenData = (TokenData) currentCard;
                    return tokenData.getPaymentInstrumentLast4();
                default:
                    break;
            }
        } catch (Exception e) {
            Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
        return null;
    }


    /**
     * Returns Card Tokens last 4 digit.
     *
     * @return Last 4 digit
     */
    public String getTokenLast4Digit() {

        try {
            switch (cardType) {
                case MDES:
                    Card card = (Card) currentCard;
                    return card.getDisplayablePanDigits();
                case VTS:
                    TokenData tokenData = (TokenData) currentCard;
                    return tokenData.getTokenLast4();
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
        return null;
    }


    /**
     * Card's current state.
     *
     * @return Card State
     */
    public CardState getCardState() {

        try {
            switch (cardType) {
                case MDES:
                    Card card = (Card) currentCard;
                    switch (card.getCardState()) {
                        case ACTIVATED:
                            return CardState.ACTIVE;
                        case SUSPENDED:
                            return CardState.SUSPENDED;
                        case NOT_ACTIVATED:
                            return CardState.INACTIVE;
                        case MARKED_FOR_DELETION:
                            return CardState.MARKED_FOR_DELETION;
                        case UNKNOWN:
                            return CardState.UNKNOWN;
                        default:
                            break;
                    }
                    break;
                case VTS:
                    TokenData tokenData = (TokenData) currentCard;
                    TokenStatus tokenStatus = TokenStatus.valueOf(tokenData.getTokenStatus());
                    switch (tokenStatus) {
                        case ACTIVE:
                            return CardState.ACTIVE;
                        case SUSPENDED:
                            return CardState.SUSPENDED;
                        case RESUME:
                            return CardState.ACTIVE;
                        case DELETED:
                            return CardState.MARKED_FOR_DELETION;
                        case OBSOLETE:
                            return CardState.INACTIVE;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
        return null;
    }


    /**
     * Returns Number of transaction credential i.e. how many transactions a card can perform.
     *
     * @return Number of transaction credentials remaining
     */
    public int getTransactionCredentialsLeft() {

        switch (cardType) {
            case MDES:
                return ((Card) currentCard).getNumberOfAvailableCredentials();
            case VTS:
                try {
                    ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                    LukInfo lukInfo = comvivaSdk.getLukInfo(this);
                    if (lukInfo != null) {
                        return lukInfo.getNoOfPaymentsRemaining();
                    }
                } catch (Exception e) {
                    return -1;
                }
                return 0;
            default:
                return 0;
        }
    }


    /**
     * CVM reset timeout in seconds.
     *
     * @return CVM Reset timeout
     */
    public int getCvmResetTimeout() {

        switch (cardType) {
            case MDES:
                return ((Card)currentCard).getCvmResetTimeout();
            case VTS:
            default:
                return 30;
        }
    }


    /**
     * Card's Unique Identification Number.
     *
     * @return Unique Id
     */
    public String getCardUniqueId() {

        try {
            switch (cardType) {
                case MDES:
                    Card card = (Card) currentCard;
                    return card.getCardId();
                case VTS:
                    return (((TokenData) currentCard).getVProvisionedTokenID());
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
        return null;
    }


    /**
     * Card's panEnrollmentId
     *
     * @return vPanEnrollmentID
     */
    public String getInstrumentId() {

        try {
            switch (cardType) {
                case MDES:
                    Card card = (Card) currentCard;
                    return card.getCardId();
                case VTS:
                    return CommonUtil.getSharedPreference(((TokenData) currentCard).getVProvisionedTokenID(), Tags.USER_DETAILS.getTag()); // getting String
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
        return null;
    }


    /**
     * Starts Contactless transaction. Throws SdkException if transaction credential is not left.
     *
     * @throws SdkException SdkException
     */
    public void startContactlessTransaction(ProcessContactlessListener processContactlessListener) throws SdkException {

        try {
            sdkData.getTransactionCompletionListener().setProcessContactlessListener(processContactlessListener);
            sdkData.getCardSelectionManagerForTransaction().setPaymentCardForTransaction(cardType, getPaymentCard(currentCard));
            //VisaPaymentSDKImpl.getInstance().selectCard(((TokenData) currentCard).getTokenKey());
            processContactlessListener.onContactlessReady();
            /*switch (cardType) {
                case MDES:
                    sdkData.getCardSelectionManagerForTransaction().setPaymentCardForTransaction(cardType,currentCard);
                    break;
                case VTS:
                    //VisaPaymentSDKImpl.getInstance().selectCard(((TokenData) currentCard).getTokenKey());
                    processContactlessListener.onContactlessReady();
                    break;
                default:
                    break;
            }*/
        }/* catch (McbpCryptoException | LdeNotInitialized | InvalidInput e) {
            throw new SdkException(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
        } catch (SessionKeysNotAvailable e) {
            throw new SdkException(SdkErrorStandardImpl.SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE);
        }*/ catch (RootDetectException e) {
            ComvivaSdk.reportFraud();
            throw new SdkException(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
        } catch (RolloverInProgressException e) {
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    /**
     * Terminates contactless transaction.
     */
    public void stopContactlessTransaction() {

        try {
            if (cardType == CardType.MDES) {
                Card card = (Card) currentCard;
                card.stopContactlessTransaction();
            }
            if (cardType == CardType.VTS) {
                sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }
    /**
     * Invoke this method just after user enters PIN.
     *
     * @param pinListener PIN Listener
     * @param pin         PIN Value
     */
  /*  public void pinEntered(PinListener pinListener, final String pin) {
        pinListener.pinEntered(ByteArray.of(pin.getBytes()));
    }*/


    /**
     * Returns that this card is default card or not.
     *
     * @return <code>true </code>This card is default card. <br>
     * <code>false </code>Not a default card.
     */
    public boolean isDefaultCard() {

        return isDefaultCard;
    }


    /**
     * Set CDCVM information.
     *
     * @param cdCvm CDCVM Information.
     */
    public void setCdCvm(CdCvm cdCvm) {

        this.cdCvm = cdCvm;
    }


    /**
     * Get CDCVM information
     *
     * @return CDCVM information
     */
    public CdCvm getCdCvm() {

        return cdCvm;
    }
}
