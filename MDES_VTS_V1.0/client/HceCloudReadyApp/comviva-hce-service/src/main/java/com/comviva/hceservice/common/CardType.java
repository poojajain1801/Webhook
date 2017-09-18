package com.comviva.hceservice.common;

/**
 * Type of card.
 */
public enum CardType {
    /**
     * Master Cards following MDES product.
     */
    MDES,

    /**
     * VISA Cards following VTS product.
     */
    VTS,

    /**
     * Card type is not supported
     */
    UNKNOWN;

    /**
     * Determines type of Card.
     * @param cardNumber Card Number
     * @return CardType
     */
    public static CardType checkCardType(String cardNumber) {
        switch (cardNumber.charAt(0)) {
            case '5':
                return MDES;

            case '4':
                return VTS;
        }
        return UNKNOWN;
    }
}
