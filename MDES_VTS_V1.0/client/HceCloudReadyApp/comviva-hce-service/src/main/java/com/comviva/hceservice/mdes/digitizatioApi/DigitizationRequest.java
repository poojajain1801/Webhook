package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Continue Digitization Request Object.
 */
public class DigitizationRequest {
    private String serviceId;
    private String taskId;
    private EligibilityReceipt eligibilityReceipt;
    private String termsAndConditionsAssetId;
    private String termsAndConditionsAcceptedTimestamp;

    /**
     * Returns Service Id.
     * @return  Service Id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Set Service Id
     * @param serviceId Service Id
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Returns Identifier for this task
     * @return Task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Set Task ID.
     * @param taskId Task ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns Terms & Condition Asset Id
     * @return AssetId
     */
    public String getTermsAndConditionsAssetId() {
        return termsAndConditionsAssetId;
    }

    /**
     * Set Terms & Condition Asset Id
     * @param termsAndConditionsAssetId Asset Id
     */
    public void setTermsAndConditionsAssetId(String termsAndConditionsAssetId) {
        this.termsAndConditionsAssetId = termsAndConditionsAssetId;
    }

    /**
     * Returns the date/time stamp when the Cardholder accepted the Terms and Conditions.
     * Must be expressed in ISO 8601 extended format as one of the following: <br/>
     *  YYYY-MM-DDThh:mm:ss[.sss]Z <br/>
     *  YYYY-MM-DDThh:mm:ss[.sss]±hh:mm Where [.sss] is optional and can be 1 to 3 digits
     * @return Timestamp when T&C accepted
     */
    public String getTermsAndConditionsAcceptedTimestamp() {
        return termsAndConditionsAcceptedTimestamp;
    }

    /**
     * Set the date/time stamp when the Cardholder accepted the Terms and Conditions.
     * @param termsAndConditionsAcceptedTimestamp Timestamp when T&C accepted
     */
    public void setTermsAndConditionsAcceptedTimestamp(String termsAndConditionsAcceptedTimestamp) {
        this.termsAndConditionsAcceptedTimestamp = termsAndConditionsAcceptedTimestamp;
    }

    /**
     * Returns the Eligibility Receipt value.
     * @return Eligibility Receipt value
     */
    public EligibilityReceipt getEligibilityReceipt() {
        return eligibilityReceipt;
    }

    /**
     * Set Eligibility Receipt value
     * @param eligibilityReceipt Eligibility Receipt value
     */
    public void setEligibilityReceipt(EligibilityReceipt eligibilityReceipt) {
        this.eligibilityReceipt = eligibilityReceipt;
    }
}
