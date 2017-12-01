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

package com.mastercard.mcbp.utils;


/**
 * Describes the parameters that can be provided from another application or website when performing
 * a Remote / DSRP payment
 */
public abstract class RemotePaymentInput {

    /**
     * Keys for parsing Remote Payment input JSON
     */
    public static final String KEY_AMOUNT = "transactionAmount";
    public static final String KEY_MERCHANT_LOGO = "merchantLogo";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_MERCHANT = "merchant";
    public static final String KEY_CALLBACK = "callback";
    public static final String KEY_IN_APP = "inApp";
    public static final String KEY_COUNTRY_CODE = "countryCode";
    public static final String KEY_CRYPTOGRAM_TYPE = "cryptogramType";
    public static final String KEY_OTHER_AMOUNT = "otherAmount";
    public static final String KEY_TRANSACTION_TYPE = "transactionType";
    public static final String KEY_UNPREDICTABLE_NUMBER = "unpredictableNumber";
    public static final String KEY_TRANSACTION_DAY = "transactionDay";
    public static final String KEY_TRANSACTION_MONTH = "transactionMonth";
    public static final String KEY_TRANSACTION_YEAR = "transactionYear";

    /**
     * Amount of transaction
     */
    private int amount;

    /**
     * Other Amount of transaction
     */
    private int otherAmount;

    /**
     * 3 digit numeric currency code
     */
    private int currency;

    /**
     * Transaction type
     */
    private int transactionType;

    /**
     * the UN to use for the transaction
     */
    private int unpredictableNumber;

    /**
     * Cryptogram type to use (DE55 or UCAF)
     */
    private String cryptogramType;

    /**
     * URL to merchant logo
     */
    private String merchantLogo;

    /**
     * Country code
     */
    private int countryCode;

    /**
     * Merchant name
     */
    private String merchant;

    /**
     * Is this an in app payment?
     */
    private boolean inApp;

    /**
     * Callback URL (optional, not required for in app)
     */
    private String callback;

    public int getTransactionDay() {
        return transactionDay;
    }

    public void setTransactionDay(int transactionDay) {
        this.transactionDay = transactionDay;
    }

    public int getTransactionMonth() {
        return transactionMonth;
    }

    public void setTransactionMonth(int transactionMonth) {
        this.transactionMonth = transactionMonth;
    }

    public int getTransactionYear() {
        return transactionYear;
    }

    public void setTransactionYear(int transactionYear) {
        this.transactionYear = transactionYear;
    }

    private int transactionDay;

    private int transactionMonth;

    private int transactionYear;

    /**
     * Checks that all mandatory fields are populated to perform a remote payment
     *
     * @return true if all mandatory fields are populated
     */
    public boolean isValidInput() {
        return amount > 0 && merchant != null && !merchant.equals("");
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMerchantLogo() {
        return merchantLogo;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public boolean isInApp() {
        return inApp;
    }

    public void setInApp(boolean inApp) {
        this.inApp = inApp;
    }

    public int getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(int otherAmount) {
        this.otherAmount = otherAmount;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public int getUnpredictableNumber() {
        return unpredictableNumber;
    }

    public void setUnpredictableNumber(int unpredictableNumber) {
        this.unpredictableNumber = unpredictableNumber;
    }

    public String getCryptogramType() {
        return cryptogramType;
    }

    public void setCryptogramType(String cryptogramType) {
        this.cryptogramType = cryptogramType;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }
}
