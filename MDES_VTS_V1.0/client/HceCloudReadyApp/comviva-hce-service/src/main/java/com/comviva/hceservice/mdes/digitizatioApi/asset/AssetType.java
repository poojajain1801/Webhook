package com.comviva.hceservice.mdes.digitizatioApi.asset;

/**
 * Type of assets.
 * Created by tarkeshwar.v on 5/25/2017.
 */
public enum AssetType {
    APPLICATION_PDF("application/pdf"),
    IMAGE_PNG("image/png"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html");

    private String type;

    AssetType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static AssetType getType(String type) {
        if(type.equalsIgnoreCase(APPLICATION_PDF.name())) {
            return APPLICATION_PDF;
        }

        if(type.equalsIgnoreCase(IMAGE_PNG.name())) {
            return IMAGE_PNG;
        }

        if(type.equalsIgnoreCase(TEXT_PLAIN.name())) {
            return TEXT_PLAIN;
        }

        if(type.equalsIgnoreCase(TEXT_HTML.name())) {
            return TEXT_HTML;
        }
        return null;
    }
}
