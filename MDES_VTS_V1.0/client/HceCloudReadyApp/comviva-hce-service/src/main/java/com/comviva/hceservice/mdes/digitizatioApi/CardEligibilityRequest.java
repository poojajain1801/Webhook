package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Request Object for Card Eligibility request.
 */
public class CardEligibilityRequest {
    private String accountNumber;
    private String expiryMonth;
    private String expiryYear;
    private String source;
    private String cardholderName;
    private String securityCode;

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
     * Returns Source of card information
     * @return Source of card information. It can have following values <br/>
     * <table>
     *     <tr>
     *         <td>CARD_ON_FILE</td>
     *         <td>Source was an existing card on file.</td>
     *     </tr>
     *     <tr>
     *         <td>CARD_ADDED_MANUALLY</td>
     *         <td>Source was a new card entered manually be the Cardholder.</td>
     *     </tr>
     *     <tr>
     *         <td>CARD_ADDED_VIA_APPLICATION</td>
     *         <td>Source was a new card added by another application (for example, Issuer banking app).</td>
     *     </tr>
     * </table>
     */
    public String getSource() {
        return source;
    }

    /**
     * Set Source of card information
     * @param source Source of card information
     */
    public void setSource(String source) {
        this.source = source;
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
}
