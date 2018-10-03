package com.comviva.hceservice.requestobjects;

import com.comviva.hceservice.digitizationApi.ConsumerEntryMode;
import com.comviva.hceservice.digitizationApi.PanSource;

/**
 * Request Object for Card Eligibility request.
 */
public class CardEligibilityRequestParam {
    private String accountNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cardholderName;
    private String securityCode;
    private PanSource panSource;
    private String locale;
    private ConsumerEntryMode consumerEntryMode;
    private String userId;

    /**
     * Returns locale.
     * @return Locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Language in which the application communicates with the customer. Based on ISO format for language (ISO 639-1)
     * and alpha-2 country code (ISO 3166-1 alpha-2). The language must be lowercase, and the country must be uppercase.
     * The language and country should be separated using a hyphen (-) e.g. en-US.
     * @param locale locale value
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Returns Consumer's input method.
     * @return Entry Mode
     */
    public ConsumerEntryMode getConsumerEntryMode() {
        return consumerEntryMode;
    }

    /**
     * Set Entry Mode
     * @param consumerEntryMode Entry Mode
     */
    public void setConsumerEntryMode(ConsumerEntryMode consumerEntryMode) {
        this.consumerEntryMode = consumerEntryMode;
    }

    /**
     * Returns source of PAN.
     * @return PAN Source
     */
    public PanSource getPanSource() {
        return panSource;
    }

    /**
     * Set source of PAN
     * @param panSource PAN Source
     */
    public void setPanSource(PanSource panSource) {
        this.panSource = panSource;
    }

    /**
     * Returns Account Primary Account Number of the card to be digitized
     * @return PAN Number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Set PAN number
     * @param accountNumber PAN Number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Returns Card's Expiry Month.
     * @return  Expiry Month
     */
    public String getExpiryMonth() {
        return expiryMonth;
    }

    /**
     * Set Card's Expiry Month.
     * @param expiryMonth   Expiry Month
     */
    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    /**
     * Returns Card's Expiry Year.
     * @return  Expiry Year
     */
    public String getExpiryYear() {
        return expiryYear;
    }

    /**
     * Set Card's Expiry Year.
     * @param expiryYear    Expiry Year
     */
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }


    /**
     * Returns Cardholder's Name.
     * @return Cardholder's Name
     */
    public String getCardholderName() {
        return cardholderName;
    }

    /**
     * Set Cardholder's Name.
     * @param cardholderName Cardholder's Name.
     */
    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    /**
     * Returns CVC2 for the card to be digitized.
     * @return CVC2
     */
    public String getSecurityCode() {
        return securityCode;
    }

    /**
     * Set CVC2 for the card to be digitized.
     * @param securityCode CVV2
     */
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
