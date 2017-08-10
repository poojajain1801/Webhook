package com.comviva.hceservice.mdes.digitizatioApi.asset;

/**
 * Media Content object.
 */
public class MediaContent {
    private AssetType assetType;
    private String data;
    private int height;
    private int width;

    /**
     * Returns What type of media this is.
     * @return Type of Asset
     */
    public AssetType getAssetType() {
        return assetType;
    }

    /**
     * Set type of asset.
     * @param assetType Asset Type
     */
    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    /**
     * The data for this item of media.
     * @return The data part of media i.e. Asset
     */
    public String getData() {
        return data;
    }

    /**
     * Set data for this item of media.
     * @param data Data/Content of Asset
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * For image assets, the width of this image. Specified in pixels.
     * @return Height of image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set height of image
     * @param height Image Height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * For image assets, the height of this image. Specified in pixels.
     * @return Image Width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set width of image
     * @param width Image Width
     */
    public void setWidth(int width) {
        this.width = width;
    }
}
