package com.comviva.hceservice.digitizationApi;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */

public class CardMetaData {
    private String longDescription;
    private String backgroundColor;
    private String contactEmail;
    private String contactName;
    private String contactNumber;
    private String foregroundColor;
    private String contactWebsite;
    private String shortDescription;
    private String labelColor;
    private String termsAndConditionsID;
    private CardData[] cardDatas;
    private UnsupportedCardVerificationTypes unsupportedCardVerificationTypes;

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getContactWebsite() {
        return contactWebsite;
    }

    public void setContactWebsite(String contactWebsite) {
        this.contactWebsite = contactWebsite;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public String getTermsAndConditionsID() {
        return termsAndConditionsID;
    }

    public void setTermsAndConditionsID(String termsAndConditionsID) {
        this.termsAndConditionsID = termsAndConditionsID;
    }

    public CardData[] getCardDatas() {
        return cardDatas;
    }

    public void setCardDatas(CardData[] cardDatas) {
        this.cardDatas = cardDatas;
    }

    public UnsupportedCardVerificationTypes getUnsupportedCardVerificationTypes() {
        return unsupportedCardVerificationTypes;
    }

    public void setUnsupportedCardVerificationTypes(UnsupportedCardVerificationTypes unsupportedCardVerificationTypes) {
        this.unsupportedCardVerificationTypes = unsupportedCardVerificationTypes;
    }
}
