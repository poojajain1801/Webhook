package com.comviva.hceservice.requestobjects;

import com.comviva.hceservice.common.PaymentCard;

/**
 * Request Object for Card Life Cycle Management Operations (Suspend, UnSuspend and Delete)
 */
public class CardLcmRequestParam {
    private CardLcmOperation cardLcmOperation;
    private PaymentCard paymentCard;
    private CardLcmReasonCode reasonCode;

    /**
     * Returns List of Tokens Unique References on which LCM is to be performed.
     * @return List of Tokens Unique References
     */
    public PaymentCard getPaymentCard() {
        return  (paymentCard);
    }

    /**
     * Set List of Tokens Unique References
     * @param paymentCard Tokens Unique References
     */
    public void setPaymentCard(PaymentCard paymentCard) {
        this.paymentCard = (paymentCard);
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
