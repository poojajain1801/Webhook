package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CardType;

/**
 * Continue Digitization Request Object.
 */
public class DigitizationRequest {
    private CardType cardType;
    private String termsAndConditionsAcceptedTimestamp;
    private String emailAddress;

    /**
     * Returns the date/time stamp when the Cardholder accepted the Terms and Conditions.
     * Must be expressed in ISO 8601 extended format as one of the following: <br/>
     * YYYY-MM-DDThh:mm:ss[.sss]Z <br/>
     * YYYY-MM-DDThh:mm:ss[.sss]±hh:mm Where [.sss] is optional and can be 1 to 3 digits
     *
     * @return Timestamp when T&C accepted
     */
    public String getTermsAndConditionsAcceptedTimestamp() {
        return termsAndConditionsAcceptedTimestamp;
    }

    /**
     * Set the date/time stamp when the Cardholder accepted the Terms and Conditions.<br>
     * Must be expressed in ISO 8601 extended format as one of the following: <br>
     * YYYY-MM-DDThh:mm:ss[.sss]Z <br>
     * YYYY-MM-DDThh:mm:ss[.sss]±hh:mm <br>
     * Where [.sss] is optional and can be 1 to 3 digits. <br>
     *
     * @param termsAndConditionsAcceptedTimestamp Timestamp when T&C accepted
     */
    public void setTermsAndConditionsAcceptedTimestamp(String termsAndConditionsAcceptedTimestamp) {
        this.termsAndConditionsAcceptedTimestamp = termsAndConditionsAcceptedTimestamp;
    }

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
