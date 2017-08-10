package com.comviva.hceservice.mdes.digitizatioApi.asset;

/**
 * What type of media this is. Specified as a MIME type.
 */
public enum AssetType {
    /**
     * For images, must be a vector PDF image.
     */
    APPLICATION_PDF("application/pdf"),
    /**
     * Image type in PNG format.
     */
    IMAGE_PNG("image/png"),
    /**
     * Plain text contents.
     */
    TEXT_PLAIN("text/plain"),
    /**
     * HTML pages.
     */
    TEXT_HTML("text/html");

    private String type;

    AssetType(String type) {
        this.type = type;
    }

    /**
     * Returns type of asset.
     * @return Asset Type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns respective AssetType enum for given type.
     * @param type Asset Type
     * @return AssetType enum instance
     */
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
