package com.comviva.hceservice.digitizationApi;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */

public enum ContentType {
    DIGITAL_CARD_ART("digitalCardArt"),

    DIGITAL_CARD_ART_BACKGROUND("digitalCardArtBackground"),

    CARD_SYMBOL("cardSymbol"),

    TERMS_AND_CONDITIONS("termsAndConditions");

    private String type;

     ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ContentType getContentType(String contentType) {
        if(contentType.equalsIgnoreCase(DIGITAL_CARD_ART.getType())) {
            return DIGITAL_CARD_ART;
        }
        if(contentType.equalsIgnoreCase(DIGITAL_CARD_ART_BACKGROUND.getType())) {
            return DIGITAL_CARD_ART_BACKGROUND;
        }
        if(contentType.equalsIgnoreCase(CARD_SYMBOL.getType())) {
            return CARD_SYMBOL;
        }
        if(contentType.equalsIgnoreCase(TERMS_AND_CONDITIONS.getType())) {
            return TERMS_AND_CONDITIONS;
        }
        return null;
    }
}
