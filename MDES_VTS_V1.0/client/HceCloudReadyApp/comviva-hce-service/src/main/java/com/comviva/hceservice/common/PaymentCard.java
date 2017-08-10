package com.comviva.hceservice.common;

import com.mastercard.mcbp.card.McbpCard;
import com.visa.cbp.sdk.facade.data.TokenData;


public class PaymentCard {
    private Object currentCard;
    private CardType cardType;

    /**
     * Sets currently selected card.
     * @param cardObj   Card Object
     */
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

    /**
     * Get currently selected card
     * @return Current card
     */
    public Object getCurrentCard() {
        return currentCard;
    }

    /**
     * Get card type.
     * @return card type
     */
    public CardType getCardType() {
        return cardType;
    }


}
