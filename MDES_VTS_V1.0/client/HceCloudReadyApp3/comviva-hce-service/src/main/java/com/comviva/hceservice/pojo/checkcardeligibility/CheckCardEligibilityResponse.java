package com.comviva.hceservice.pojo.checkcardeligibility;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Response Object of Card Eligibility Request API.
 */
public class CheckCardEligibilityResponse implements Serializable {

    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("message")
    @Expose
    private String responseMessage;
    @SerializedName("responseHost")
    @Expose
    private String responseHost;
    @SerializedName("responseId")
    @Expose
    private String responseId;
    @SerializedName("termsAndConditionsAssetId")
    @Expose
    private String termsAndConditionsAssetId;
    @SerializedName("serviceId")
    @Expose
    private String serviceId;
    @SerializedName("eligibilityReceipt")
    @Expose
    private EligibilityReceipt eligibilityReceipt;
    @SerializedName("applicableCardInfo")
    @Expose
    private ApplicableCardInfo applicableCardInfo;


    /**
     * Returns Service ID. This ID is used in digitize api
     *
     * @return Service Id
     */
    public String getServiceId() {

        return serviceId;
    }


    /**
     * Set Service ID.
     *
     * @param serviceId Service ID
     */
    public void setServiceId(String serviceId) {

        this.serviceId = serviceId;
    }


    /**
     * Returns host that originated the response.
     *
     * @return Response Host
     */
    public String getResponseHost() {

        return responseHost;
    }


    /**
     * Set the host that originated the response.
     *
     * @param responseHost Response Host
     */
    public void setResponseHost(String responseHost) {

        this.responseHost = responseHost;
    }


    /**
     * Returns Unique identifier for the response.
     *
     * @return Unique identifier
     */
    public String getResponseId() {

        return responseId;
    }


    /**
     * Set Unique identifier for the response
     *
     * @param responseId Unique identifier
     */
    public void setResponseId(String responseId) {

        this.responseId = responseId;
    }


    /**
     * Returns the Terms and Conditions to be presented to the Cardholder. Provided as an Asset ID
     *
     * @return Terms and Conditions asset id
     */
    public String getTermsAndConditionsAssetId() {

        return termsAndConditionsAssetId;
    }


    /**
     * Set Terms and Conditions asset id.
     *
     * @param termsAndConditionsAssetId Terms and Conditions Asset Id
     */
    public void setTermsAndConditionsAssetId(String termsAndConditionsAssetId) {

        this.termsAndConditionsAssetId = termsAndConditionsAssetId;
    }


    /**
     * Contains the Eligibility Receipt, provided by MDES.
     *
     * @return EligibilityReceipt object.
     */
    public EligibilityReceipt getEligibilityReceipt() {

        return eligibilityReceipt;
    }


    /**
     * Set Eligibility Receipt.
     *
     * @param eligibilityReceipt EligibilityReceipt object
     */
    public void setEligibilityReceipt(EligibilityReceipt eligibilityReceipt) {

        this.eligibilityReceipt = eligibilityReceipt;
    }


    /**
     * Returns Applicable Card Info. Applicable Card Info contains flags to indicate what additional
     * card information is applicable for this product and may be provided in the Digitize request.
     *
     * @return Applicable Card Info
     */
    public ApplicableCardInfo getApplicableCardInfo() {

        return applicableCardInfo;
    }


    /**
     * Set Applicable Card Info
     *
     * @param applicableCardInfo Applicable Card Info
     */
    public void setApplicableCardInfo(ApplicableCardInfo applicableCardInfo) {

        this.applicableCardInfo = applicableCardInfo;
    }


    public String getResponseCode() {

        return responseCode;
    }


    public String getResponseMessage() {

        return responseMessage;
    }
}
