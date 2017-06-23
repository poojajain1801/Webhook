package com.comviva.hceservice.mdes.digitizatioApi;

import java.io.Serializable;

/** Response of Card Eligibility Request API.
 * Created by tarkeshwar.v on 5/24/2017.
 */
public class CardEligibilityResponse implements Serializable {
    public class ApplicableCardInfo implements Serializable {
        private boolean isSecurityCodeApplicable;

        public boolean isSecurityCodeApplicable() {
            return isSecurityCodeApplicable;
        }

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getResponseHost() {
        return responseHost;
    }

    public void setResponseHost(String responseHost) {
        this.responseHost = responseHost;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getTermsAndConditionsAssetId() {
        return termsAndConditionsAssetId;
    }

    public void setTermsAndConditionsAssetId(String termsAndConditionsAssetId) {
        this.termsAndConditionsAssetId = termsAndConditionsAssetId;
    }

    public EligibilityReceipt getEligibilityReceipt() {
        return eligibilityReceipt;
    }

    public void setEligibilityReceipt(EligibilityReceipt eligibilityReceipt) {
        this.eligibilityReceipt = eligibilityReceipt;
    }

    public ApplicableCardInfo getApplicableCardInfo() {
        return applicableCardInfo;
    }

    public void setApplicableCardInfo(ApplicableCardInfo applicableCardInfo) {
        this.applicableCardInfo = applicableCardInfo;
    }
}
