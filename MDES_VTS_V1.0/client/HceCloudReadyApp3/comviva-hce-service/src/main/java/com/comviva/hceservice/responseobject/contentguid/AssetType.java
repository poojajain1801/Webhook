package com.comviva.hceservice.responseobject.contentguid;

import com.comviva.hceservice.common.Tags;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * What type of media this is. Specified as a MIME type.
 */
public enum AssetType {
    /**
     * For images, must be a vector PDF image.
     */
    @SerializedName("application/pdf")
    @Expose
    APPLICATION_PDF,
    /**
     * Image type in PNG format.
     */
    @SerializedName("image/png")
    @Expose
    IMAGE_PNG,
    /**
     * Plain text contents.
     */
    @SerializedName("text/plain")
    @Expose
    TEXT_PLAIN,
    @SerializedName("digitalCardArt")
    DIGITAL_CARD_ART,
    @SerializedName("digitalCardArtBackground")
    DIGITAL_CARD_ART_BACKGROUND,
    @SerializedName("cardSymbol")
    CARD_SYMBOL,
    @SerializedName("termsAndConditions")
    TERMS_AND_CONDITIONS,
    /**
     * HTML pages.
     */
    @SerializedName("text/html")
    @Expose
    TEXT_HTML;


    /**
     * Returns respective AssetType enum for given type.
     *
     * @param type Asset Type
     * @return AssetType enum instance
     */
    public static AssetType getType(String type) {

        if (type.equalsIgnoreCase(IMAGE_PNG.name())) {
            return IMAGE_PNG;
        }
        if (type.equalsIgnoreCase(TEXT_PLAIN.name())) {
            return TEXT_PLAIN;
        }
        if (type.equalsIgnoreCase(TEXT_HTML.name())) {
            return TEXT_HTML;
        }

        // these tags are for content type in VISA
        if (type.equalsIgnoreCase(Tags.DIGITAL_CARD_ART.getTag())) {
            return DIGITAL_CARD_ART;
        }
        if (type.equalsIgnoreCase(Tags.DIGITAL_CARD_ART_BACKGROUND.getTag())) {
            return DIGITAL_CARD_ART_BACKGROUND;
        }
        if (type.equalsIgnoreCase(Tags.CARD_SYMBOL.getTag())) {
            return CARD_SYMBOL;
        }
        if (type.equalsIgnoreCase(Tags.TERMS_CONDITION.getTag())) {
            return TERMS_AND_CONDITIONS;
        }
        return null;
    }
}
