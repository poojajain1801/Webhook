package com.comviva.hceservice.requestobjects;

import com.comviva.hceservice.common.CardType;

/**
 * Continue Digitization Request Object.
 */
public class DigitizationRequestParam {
    private CardType cardType;
    private String emailAddress;


    /**
     * Return email id.
     * @return Email Id
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Set Client-provided email address linked to their wallet account login.
     * @param emailAddress Email Id
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Return Type of Card to be digitized
     * @return Card Type
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Set Type of Card to be digitized
     * @param cardType Card Type
     */
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
}
