package com.comviva.hceservice.util;

/**
 * Utility used to check card validity on the basis of Luhn Check.
 */
public class LuhnUtil {
    /**
     * Validate card's according to Luhn Check.
     * @param cardNumber    Card Number to be validated
     * @return  <code>true </code>Card Number passed luhn check<br>
     *     <code>false </code>Card number is invalid
     */
    public static boolean checkLuhn(String cardNumber) {
        int length = cardNumber.length();
        int sum = 0;
        int d;
        for (int i = 0; i < length; i++) {
            d = Character.getNumericValue(cardNumber.charAt(length - i - 1));
            if (i % 2 == 1) {
                d = d * 2;
                if (d > 9) {
                    d = d - 9;
                }
                sum += d;
            } else {
                sum += d;
            }
        }
        return sum % 10 == 0;
    }
}
