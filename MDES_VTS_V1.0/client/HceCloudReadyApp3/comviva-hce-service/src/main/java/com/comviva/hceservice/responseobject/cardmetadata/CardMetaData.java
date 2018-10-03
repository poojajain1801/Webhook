package com.comviva.hceservice.responseobject.cardmetadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CardMetaData implements Serializable {

    @SerializedName("termsAndConditionsID")
    @Expose
    private String termsAndConditionsID;
    @SerializedName("labelColor")
    @Expose
    private String labelColor;
    @SerializedName("contactEmail")
    @Expose
    private String contactEmail;
    @SerializedName("contactName")
    @Expose
    private String contactName;
    @SerializedName("contactNumber")
    @Expose
    private String contactNumber;
    @SerializedName("contactWebsite")
    @Expose
    private String contactWebsite;
    @SerializedName("longDescription")
    @Expose
    private String longDescription;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("foregroundColor")
    @Expose
    private String foregroundColor;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("cardData")
    @Expose
    private List<CardData> cardData;
    @SerializedName("aidInfo")
    @Expose
    private List<UnsupportedCardVerificationTypes> unsupportedCardVerificationTypes;


    public List<CardData> getCardData() {

        return cardData;
    }


    public List<UnsupportedCardVerificationTypes> getUnsupportedCardVerificationTypes() {

        return unsupportedCardVerificationTypes;
    }


    public String getContactEmail() {

        return contactEmail;
    }


    public String getContactName() {

        return contactName;
    }


    public String getContactNumber() {

        return contactNumber;
    }


    public String getContactWebsite() {

        return contactWebsite;
    }


    public String getLongDescription() {

        return longDescription;
    }


    public String getTermsAndConditionsID() {

        return termsAndConditionsID;
    }


    public String getLabelColor() {

        return labelColor;
    }


    public String getShortDescription() {

        return shortDescription;
    }


    public String getForegroundColor() {

        return foregroundColor;
    }


    public String getBackgroundColor() {

        return backgroundColor;
    }
}
