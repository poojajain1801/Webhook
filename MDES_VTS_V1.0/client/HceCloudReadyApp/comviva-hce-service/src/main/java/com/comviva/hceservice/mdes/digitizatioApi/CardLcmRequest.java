package com.comviva.hceservice.mdes.digitizatioApi;

import com.comviva.hceservice.common.CardLcmOperation;

import java.util.ArrayList;

/**
 * Created by tarkeshwar.v on 6/14/2017.
 */

public class CardLcmRequest {
    private CardLcmOperation cardLcmOperation;
    private ArrayList<String> tokenUniqueReferences;
    private CardLcmReasonCode reasonCode;

    public ArrayList<String> getTokenUniqueReferences() {
        return tokenUniqueReferences;
    }

    public void setTokenUniqueReferences(ArrayList<String> tokenUniqueReferences) {
        this.tokenUniqueReferences = tokenUniqueReferences;
    }

    public CardLcmReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(CardLcmReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    public CardLcmOperation getCardLcmOperation() {
        return cardLcmOperation;
    }

    public void setCardLcmOperation(CardLcmOperation cardLcmOperation) {
        this.cardLcmOperation = cardLcmOperation;
    }
}
