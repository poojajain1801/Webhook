package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Continue Digitization Request
 * Created by tarkeshwar.v on 5/25/2017.
 */
public class DigitizationRequest {
    private String serviceId;
    private String taskId;
    private EligibilityReceipt eligibilityReceipt;
    private String termsAndConditionsAssetId;
    private String termsAndConditionsAcceptedTimestamp;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTermsAndConditionsAssetId() {
        return termsAndConditionsAssetId;
    }

    public void setTermsAndConditionsAssetId(String termsAndConditionsAssetId) {
        this.termsAndConditionsAssetId = termsAndConditionsAssetId;
    }

    public String getTermsAndConditionsAcceptedTimestamp() {
        return termsAndConditionsAcceptedTimestamp;
    }

    public void setTermsAndConditionsAcceptedTimestamp(String termsAndConditionsAcceptedTimestamp) {
        this.termsAndConditionsAcceptedTimestamp = termsAndConditionsAcceptedTimestamp;
    }

    public EligibilityReceipt getEligibilityReceipt() {
        return eligibilityReceipt;
    }

    public void setEligibilityReceipt(EligibilityReceipt eligibilityReceipt) {
        this.eligibilityReceipt = eligibilityReceipt;
    }
}
