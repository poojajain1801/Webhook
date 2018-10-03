package com.comviva.hceservice.responseobject.contentguid;

import java.io.Serializable;

/**
 * ContentGuid class contains assets information of card GUID information or other asset's value and type.
 */
public class ContentGuid implements Serializable {
    private String altText;
    private AssetType contentType ;
    private MediaContent[] content;

    public ContentGuid() {
        // INTENSIONALLY LEFT BLANK
    }
    /**
     * Returns Alternate text for the asset.
     * @return Alternate Text
     */
    public String getAltText() {
        return altText;
    }

    /**
     * Set Alternate text for the asset.
     * @param altText Alternate text
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    /**
     * Returns category of the content.
     * @return Content Type
     */
    public AssetType getContentType() {
        return contentType;
    }

    /**
     * Set category of the content.
     * @param contentType Content Type
     */
    public void setContentType(AssetType contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns Asset's content.
     * @return Asset's content
     */
    public MediaContent[] getContent() {
        return (content);
    }

    /**
     * Set Asset's content.
     * @param content Asset's content
     */
    public void setContent(MediaContent[] content) {
        this.content = (content);
    }
}
