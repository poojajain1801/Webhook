package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.PaymentCard;

import java.util.ArrayList;

/**
 * Request Object for Card Life Cycle Management Operations (Suspend, UnSuspend and Delete)
 */
public class CardLcmRequest {
    private CardLcmOperation cardLcmOperation;
    private ArrayList<PaymentCard> paymentCards;
    private CardLcmReasonCode reasonCode;

    /**
     * Returns List of Tokens Unique References on which LCM is to be performed.
     * @return List of Tokens Unique References
     */
    public ArrayList<PaymentCard> getPaymentCards() {
        return paymentCards;
    }

    /**
     * Set List of Tokens Unique References
     * @param paymentCards List of Tokens Unique References
     */
    public void setPaymentCards(ArrayList<PaymentCard> paymentCards) {
        this.paymentCards = paymentCards;
    }

    /**
     * Returns reason code for the LCM operation.
     * @return CardLcmReasonCode
     */
    public CardLcmReasonCode getReasonCode() {
        return reasonCode;
    }

    /**
     * Set LCM Reason Code.
     * @param reasonCode LCM Reason Code
     */
    public void setReasonCode(CardLcmReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * Returns Card LCM operation.
     * @return Card LCM operation
     */
    public CardLcmOperation getCardLcmOperation() {
        return cardLcmOperation;
    }

    /**
     * Set Card LCM operation.
     * @param cardLcmOperation Card LCM operation
     */
    public void setCardLcmOperation(CardLcmOperation cardLcmOperation) {
        this.cardLcmOperation = cardLcmOperation;
    }
}
