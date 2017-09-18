package com.comviva.hceservice.digitizationApi;

import java.io.Serializable;

/**
 * Response Object of Card Eligibility Request API.
 */
public class CardEligibilityResponse implements Serializable {
    /**
     * Contains flags to indicate what additional card information is applicable for this product and may be provided in the Digitize request.
     */
    public class ApplicableCardInfo implements Serializable {
        private boolean isSecurityCodeApplicable;

        /**
         * Whether a CVC2 is applicable for this card product being digitized
         * @return <ul>
         *     <li><code>true  </code>CVC2 is applicable</li>
         *     <li><code>false </code>CVC2 is not applicable</li>
         * </ul>
         */
        public boolean isSecurityCodeApplicable() {
            return isSecurityCodeApplicable;
        }

        /**
         * Set Security Code Applicability.
         * @param securityCodeApplicable <ul>
         *     <li><code>true  </code>CVC2 is applicable</li>
         *     <li><code>false </code>CVC2 is not applicable</li>
         * </ul>
         */
        public void setSecurityCodeApplicable(boolean securityCodeApplicable) {
            isSecurityCodeApplicable = securityCodeApplicable;
        }
    }

    private String responseHost;
    private String responseId;
    private String termsAndConditionsAssetId;
    private String serviceId;
    private EligibilityReceipt eligibilityReceipt;
    private ApplicableCardInfo applicableCardInfo;

    public CardEligibilityResponse() {
        eligibilityReceipt = new EligibilityReceipt();
        applicableCardInfo = new ApplicableCardInfo();
    }

    /**
     * Returns Service ID. This ID is used in digitize api
     * @return Service Id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Set Service ID.
     * @param serviceId Service ID
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Returns host that originated the response.
     * @return  Response Host
     */
    public String getResponseHost() {
        return responseHost;
    }

    /**
     * Set the host that originated the response.
     * @param responseHost Response Host
     */
    public void setResponseHost(String responseHost) {
        this.responseHost = responseHost;
    }

    /**
     * Returns Unique identifier for the response.
     * @return Unique identifier
     */
    public String getResponseId() {
        return responseId;
    }

    /**
     * Set Unique identifier for the response
     * @param responseId Unique identifier
     */
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    /**
     * Returns the Terms and Conditions to be presented to the Cardholder. Provided as an Asset ID
     * @return Terms and Conditions asset id
     */
    public String getTermsAndConditionsAssetId() {
        return termsAndConditionsAssetId;
    }

    /**
     * Set Terms and Conditions asset id.
     * @param termsAndConditionsAssetId Terms and Conditions Asset Id
     */
    public void setTermsAndConditionsAssetId(String termsAndConditionsAssetId) {
        this.termsAndConditionsAssetId = termsAndConditionsAssetId;
    }

    /**
     * Contains the Eligibility Receipt, provided by MDES.
     * @return EligibilityReceipt object.
     */
    public EligibilityReceipt getEligibilityReceipt() {
        return eligibilityReceipt;
    }

    /**
     * Set Eligibility Receipt.
     * @param eligibilityReceipt EligibilityReceipt object
     */
    public void setEligibilityReceipt(EligibilityReceipt eligibilityReceipt) {
        this.eligibilityReceipt = eligibilityReceipt;
    }

    /**
     * Returns Applicable Card Info. Applicable Card Info contains flags to indicate what additional
     * card information is applicable for this product and may be provided in the Digitize request.
     * @return Applicable Card Info
     */
    public ApplicableCardInfo getApplicableCardInfo() {
        return applicableCardInfo;
    }

    /**
     * Set Applicable Card Info
     * @param applicableCardInfo    Applicable Card Info
     */
    public void setApplicableCardInfo(ApplicableCardInfo applicableCardInfo) {
        this.applicableCardInfo = applicableCardInfo;
    }
}
