package com.comviva.hceservice.responseobject.cardmetadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductConfig implements Serializable{

    @SerializedName("brandLogoAssetId")
    @Expose
    private String brandLogoAssetId;
    @SerializedName("isCoBranded")
    @Expose
    private String isCoBranded;
    @SerializedName("coBrandName")
    @Expose
    private String coBrandName;
    @SerializedName("coBrandLogoAssetId")
    @Expose
    private String coBrandLogoAssetId;
    @SerializedName("cardBackgroundCombinedAssetId")
    @Expose
    private String cardBackgroundCombinedAssetId;
    @SerializedName("foregroundColor")
    @Expose
    private String foregroundColor;
    @SerializedName("issuerName")
    @Expose
    private String issuerName;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("longDescription")
    @Expose
    private String longDescription;
    @SerializedName("issuerLogoAssetId")
    @Expose
    private String issuerLogoAssetId;
    @SerializedName("iconAssetId")
    @Expose
    private String iconAssetId;
    @SerializedName("customerServiceUrl")
    @Expose
    private String customerServiceUrl;
    @SerializedName("termsAndConditionsUrl")
    @Expose
    private String termsAndConditionsUrl;
    @SerializedName("privacyPolicyUrl")
    @Expose
    private String privacyPolicyUrl;
    @SerializedName("issuerProductConfigCode")
    @Expose
    private String issuerProductConfigCode;


    public String getBrandLogoAssetId() {

        return brandLogoAssetId;
    }


    public String getIsCoBranded() {

        return isCoBranded;
    }


    public String getCoBrandName() {

        return coBrandName;
    }


    public String getCoBrandLogoAssetId() {

        return coBrandLogoAssetId;
    }


    public String getCardBackgroundCombinedAssetId() {

        return cardBackgroundCombinedAssetId;
    }


    public String getForegroundColor() {

        return foregroundColor;
    }


    public String getIssuerName() {

        return issuerName;
    }


    public String getShortDescription() {

        return shortDescription;
    }


    public String getLongDescription() {

        return longDescription;
    }


    public String getIssuerLogoAssetId() {

        return issuerLogoAssetId;
    }


    public String getIconAssetId() {

        return iconAssetId;
    }


    public String getCustomerServiceUrl() {

        return customerServiceUrl;
    }


    public String getTermsAndConditionsUrl() {

        return termsAndConditionsUrl;
    }


    public String getPrivacyPolicyUrl() {

        return privacyPolicyUrl;
    }


    public String getIssuerProductConfigCode() {

        return issuerProductConfigCode;
    }
}
