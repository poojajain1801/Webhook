package com.comviva.hceservice.common;

import com.mastercard.mcbp.card.McbpCard;
import com.visa.cbp.sdk.facade.data.TokenData;

public class PaymentCard {
    private Object currentCard;
    private CardType cardType;

    public void setCurrentCard(Object cardObj) {
        currentCard = cardObj;
        if(cardObj instanceof McbpCard) {
            cardType = CardType.MDES;
        } else if(cardObj instanceof TokenData) {
            cardType = CardType.VTS;
        } else {
            currentCard = null;
            cardType = null;
        }
    }

    public Object getCurrentCard() {
        return currentCard;
    }

    public CardType getCardType() {
        return cardType;
    }


}
