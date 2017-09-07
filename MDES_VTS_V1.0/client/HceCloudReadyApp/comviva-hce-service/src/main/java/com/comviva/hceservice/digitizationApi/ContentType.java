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

    private ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
