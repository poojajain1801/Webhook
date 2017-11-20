/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mobile_api.currency;

import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to get the ISO 4217 Numeric Codes
 * Although Java supports it, the ability to parse through numeric codes is not available on
 * Android.
 * <p/>
 * Source: ISO 4217:2015 (http://www.iso.org/iso/home/standards/currency_codes.htm)
 */
public enum Iso4217CurrencyUtils {
    INSTANCE;

    private static Map<String, String> sCodes = new HashMap<>();

    /**
     * Get the currency code from a numeric value
     *
     * @param numericCode The Numeric Code as String
     * @return The Currency Code
     */
    public static String getCodeFromNumericValue(final String numericCode) {
        return sCodes.get(numericCode);
    }

    /**
     * Utility function to calculate the fraction digits for a given currency. If the currency is
     * null, fraction digits will be returned as 0
     *
     * @return The fraction digit for this currency. 0, if the currency is null.
     */
    private static int getFractionDigits(final Currency currency) {
        final int fractionDigits;
        if (currency != null) {
            fractionDigits = currency.getDefaultFractionDigits();
        } else {
            fractionDigits = 0;
        }
        return fractionDigits;
    }

    /**
     * Utility function to get the Currency from the numeric code (e.g. EURO has code 978).
     *
     * @param code The ISO 4217 Numeric code as byte[]
     * @return The Currency associated with that numeric code. Null if the currency could not be
     * found
     */
    public static Currency getCurrencyByCode(final byte[] code) {
        try {
            // We need to take exact the characters 1,2,3 excluding 0 and any other trailer.
            // It seems in the db this is stored as 3 bytes value, so we need to remove trailer
            // zeroes.
            final String value = ByteArray.of(code).toHexString().substring(1, 4);
            String currencyNumericValue = Iso4217CurrencyUtils.getCodeFromNumericValue(value);
            if (currencyNumericValue == null) {
                return null;
            }
            return Currency.getInstance(currencyNumericValue);
        } catch (final IllegalArgumentException e) {
            // We could not find the currency
            return null;
        }
    }

    /**
     * Utility function to calculate the exact amount for a given currency code.
     * For example, 5199 USD is converted into 55,99 USD as USD has 2 fraction digits
     *
     * @param amount   The transaction amount as BCD data (as received in the C-APDU)
     * @param currency The currency for the amount
     * @return The amount as double
     */
    public static double convertBcdAmountToDouble(final byte[] amount, final Currency currency) {
        final int fractionDigits = getFractionDigits(currency);
        final double amountAsDouble = (double) Long.valueOf(ByteArray.of(amount).toHexString(), 10);
        return amountAsDouble / (fractionDigits > 0 ? Math.pow(10, fractionDigits) : 1);
    }

    /**
     * Utility function to calculate the exact amount for a given currency code.
     * Please note that this function assumes binary data as Input
     *
     * @param amount   The transaction amount as binary data. This is used for Amount Other in the
     *                 transaction data
     * @param currency The currency for the amount
     * @return The amount as double
     */
    public static double convertBinaryAmountToDouble(final byte[] amount, final Currency currency) {
        final int fractionDigits = getFractionDigits(currency);
        long amountAsLong = 0;
        for (int i = 0; i < amount.length; i++) {
            // AND with 0x00FF ensures that the positive sign is preserved!
            amountAsLong += ((amount[amount.length - 1 - i] & 0x00FF) << (i * 8));
        }
        return (double) amountAsLong / (fractionDigits > 0 ? Math.pow(10, fractionDigits) : 1);
    }

    /**
     * Iso 4217:2015 Table
     */
    static {
        sCodes.put("971", "AFN");
        sCodes.put("978", "EUR");
        sCodes.put("008", "ALL");
        sCodes.put("012", "DZD");
        sCodes.put("840", "USD");
        sCodes.put("978", "EUR");
        sCodes.put("973", "AOA");
        sCodes.put("951", "XCD");

        sCodes.put("951", "XCD");
        sCodes.put("032", "ARS");
        sCodes.put("051", "AMD");
        sCodes.put("533", "AWG");
        sCodes.put("036", "AUD");
        sCodes.put("978", "EUR");
        sCodes.put("944", "AZN");
        sCodes.put("044", "BSD");
        sCodes.put("048", "BHD");
        sCodes.put("050", "BDT");
        sCodes.put("052", "BBD");
        sCodes.put("974", "BYR");
        sCodes.put("978", "EUR");
        sCodes.put("084", "BZD");
        sCodes.put("952", "XOF");
        sCodes.put("060", "BMD");
        sCodes.put("064", "BTN");
        sCodes.put("356", "INR");
        sCodes.put("068", "BOB");
        sCodes.put("984", "BOV");
        sCodes.put("840", "USD");
        sCodes.put("977", "BAM");
        sCodes.put("072", "BWP");
        sCodes.put("578", "NOK");
        sCodes.put("986", "BRL");
        sCodes.put("840", "USD");
        sCodes.put("096", "BND");
        sCodes.put("975", "BGN");
        sCodes.put("952", "XOF");
        sCodes.put("108", "BIF");
        sCodes.put("132", "CVE");
        sCodes.put("116", "KHR");
        sCodes.put("950", "XAF");
        sCodes.put("124", "CAD");
        sCodes.put("136", "KYD");
        sCodes.put("950", "XAF");
        sCodes.put("950", "XAF");
        sCodes.put("990", "CLF");
        sCodes.put("152", "CLP");
        sCodes.put("156", "CNY");
        sCodes.put("036", "AUD");
        sCodes.put("036", "AUD");
        sCodes.put("170", "COP");
        sCodes.put("970", "COU");
        sCodes.put("174", "KMF");
        sCodes.put("976", "CDF");
        sCodes.put("950", "XAF");
        sCodes.put("554", "NZD");
        sCodes.put("188", "CRC");
        sCodes.put("952", "XOF");
        sCodes.put("191", "HRK");
        sCodes.put("931", "CUC");
        sCodes.put("192", "CUP");
        sCodes.put("532", "ANG");
        sCodes.put("978", "EUR");
        sCodes.put("203", "CZK");
        sCodes.put("208", "DKK");
        sCodes.put("262", "DJF");
        sCodes.put("951", "XCD");
        sCodes.put("214", "DOP");
        sCodes.put("840", "USD");
        sCodes.put("818", "EGP");
        sCodes.put("222", "SVC");
        sCodes.put("840", "USD");
        sCodes.put("950", "XAF");
        sCodes.put("232", "ERN");
        sCodes.put("978", "EUR");
        sCodes.put("230", "ETB");
        sCodes.put("978", "EUR");
        sCodes.put("238", "FKP");
        sCodes.put("208", "DKK");
        sCodes.put("242", "FJD");
        sCodes.put("978", "EUR");
        sCodes.put("978", "EUR");
        sCodes.put("978", "EUR");
        sCodes.put("953", "XPF");
        sCodes.put("978", "EUR");
        sCodes.put("950", "XAF");
        sCodes.put("270", "GMD");
        sCodes.put("981", "GEL");
        sCodes.put("978", "EUR");
        sCodes.put("936", "GHS");
        sCodes.put("292", "GIP");
        sCodes.put("978", "EUR");
        sCodes.put("208", "DKK");
        sCodes.put("951", "XCD");
        sCodes.put("978", "EUR");
        sCodes.put("840", "USD");
        sCodes.put("320", "GTQ");
        sCodes.put("826", "GBP");
        sCodes.put("324", "GNF");
        sCodes.put("952", "XOF");
        sCodes.put("328", "GYD");
        sCodes.put("332", "HTG");
        sCodes.put("840", "USD");
        sCodes.put("036", "AUD");
        sCodes.put("978", "EUR");
        sCodes.put("340", "HNL");
        sCodes.put("344", "HKD");
        sCodes.put("348", "HUF");
        sCodes.put("352", "ISK");
        sCodes.put("356", "INR");
        sCodes.put("360", "IDR");
        sCodes.put("960", "XDR");
        sCodes.put("364", "IRR");
        sCodes.put("368", "IQD");
        sCodes.put("978", "EUR");
        sCodes.put("826", "GBP");
        sCodes.put("376", "ILS");
        sCodes.put("978", "EUR");
        sCodes.put("388", "JMD");
        sCodes.put("392", "JPY");
        sCodes.put("826", "GBP");
        sCodes.put("400", "JOD");
        sCodes.put("398", "KZT");
        sCodes.put("404", "KES");
        sCodes.put("036", "AUD");
        sCodes.put("408", "KPW");
        sCodes.put("410", "KRW");
        sCodes.put("414", "KWD");
        sCodes.put("417", "KGS");
        sCodes.put("418", "LAK");
        sCodes.put("978", "EUR");
        sCodes.put("422", "LBP");
        sCodes.put("426", "LSL");
        sCodes.put("710", "ZAR");
        sCodes.put("430", "LRD");
        sCodes.put("434", "LYD");
        sCodes.put("756", "CHF");
        sCodes.put("978", "EUR");
        sCodes.put("978", "EUR");
        sCodes.put("446", "MOP");
        sCodes.put("807", "MKD");
        sCodes.put("969", "MGA");
        sCodes.put("454", "MWK");
        sCodes.put("458", "MYR");
        sCodes.put("462", "MVR");
        sCodes.put("952", "XOF");
        sCodes.put("978", "EUR");
        sCodes.put("840", "USD");
        sCodes.put("978", "EUR");
        sCodes.put("478", "MRO");
        sCodes.put("480", "MUR");
        sCodes.put("978", "EUR");
        sCodes.put("965", "XUA");
        sCodes.put("484", "MXN");
        sCodes.put("979", "MXV");
        sCodes.put("840", "USD");
        sCodes.put("498", "MDL");
        sCodes.put("978", "EUR");
        sCodes.put("496", "MNT");
        sCodes.put("978", "EUR");
        sCodes.put("951", "XCD");
        sCodes.put("504", "MAD");
        sCodes.put("943", "MZN");
        sCodes.put("104", "MMK");
        sCodes.put("516", "NAD");
        sCodes.put("710", "ZAR");
        sCodes.put("036", "AUD");
        sCodes.put("524", "NPR");
        sCodes.put("978", "EUR");
        sCodes.put("953", "XPF");
        sCodes.put("554", "NZD");
        sCodes.put("558", "NIO");
        sCodes.put("952", "XOF");
        sCodes.put("566", "NGN");
        sCodes.put("554", "NZD");
        sCodes.put("036", "AUD");
        sCodes.put("840", "USD");
        sCodes.put("578", "NOK");
        sCodes.put("512", "OMR");
        sCodes.put("586", "PKR");
        sCodes.put("840", "USD");
        sCodes.put("", "");
        sCodes.put("590", "PAB");
        sCodes.put("840", "USD");
        sCodes.put("598", "PGK");
        sCodes.put("600", "PYG");
        sCodes.put("604", "PEN");
        sCodes.put("608", "PHP");
        sCodes.put("554", "NZD");
        sCodes.put("985", "PLN");
        sCodes.put("978", "EUR");
        sCodes.put("840", "USD");
        sCodes.put("634", "QAR");
        sCodes.put("978", "EUR");
        sCodes.put("946", "RON");
        sCodes.put("643", "RUB");
        sCodes.put("646", "RWF");
        sCodes.put("978", "EUR");
        sCodes.put("654", "SHP");
        sCodes.put("951", "XCD");
        sCodes.put("951", "XCD");
        sCodes.put("978", "EUR");
        sCodes.put("978", "EUR");
        sCodes.put("951", "XCD");
        sCodes.put("882", "WST");
        sCodes.put("978", "EUR");
        sCodes.put("678", "STD");
        sCodes.put("682", "SAR");
        sCodes.put("952", "XOF");
        sCodes.put("941", "RSD");
        sCodes.put("690", "SCR");
        sCodes.put("694", "SLL");
        sCodes.put("702", "SGD");
        sCodes.put("532", "ANG");
        sCodes.put("994", "XSU");
        sCodes.put("978", "EUR");
        sCodes.put("978", "EUR");
        sCodes.put("090", "SBD");
        sCodes.put("706", "SOS");
        sCodes.put("710", "ZAR");
        sCodes.put("", "");
        sCodes.put("728", "SSP");
        sCodes.put("978", "EUR");
        sCodes.put("144", "LKR");
        sCodes.put("938", "SDG");
        sCodes.put("968", "SRD");
        sCodes.put("578", "NOK");
        sCodes.put("748", "SZL");
        sCodes.put("752", "SEK");
        sCodes.put("947", "CHE");
        sCodes.put("756", "CHF");
        sCodes.put("948", "CHW");
        sCodes.put("760", "SYP");
        sCodes.put("901", "TWD");
        sCodes.put("972", "TJS");
        sCodes.put("834", "TZS");
        sCodes.put("764", "THB");
        sCodes.put("840", "USD");
        sCodes.put("952", "XOF");
        sCodes.put("554", "NZD");
        sCodes.put("776", "TOP");
        sCodes.put("780", "TTD");
        sCodes.put("788", "TND");
        sCodes.put("949", "TRY");
        sCodes.put("934", "TMT");
        sCodes.put("840", "USD");
        sCodes.put("036", "AUD");
        sCodes.put("800", "UGX");
        sCodes.put("980", "UAH");
        sCodes.put("784", "AED");
        sCodes.put("826", "GBP");
        sCodes.put("840", "USD");
        sCodes.put("840", "USD");
        sCodes.put("997", "USN");
        sCodes.put("940", "UYI");
        sCodes.put("858", "UYU");
        sCodes.put("860", "UZS");
        sCodes.put("548", "VUV");
        sCodes.put("937", "VEF");
        sCodes.put("704", "VND");
        sCodes.put("840", "USD");
        sCodes.put("840", "USD");
        sCodes.put("953", "XPF");
        sCodes.put("504", "MAD");
        sCodes.put("886", "YER");
        sCodes.put("967", "ZMW");
        sCodes.put("932", "ZWL");
        sCodes.put("955", "XBA");
        sCodes.put("956", "XBB");
        sCodes.put("957", "XBC");
        sCodes.put("958", "XBD");
        sCodes.put("963", "XTS");
        sCodes.put("999", "XXX");
        sCodes.put("959", "XAU");
        sCodes.put("964", "XPD");
        sCodes.put("962", "XPT");
        sCodes.put("961", "XAG");
    }

}
