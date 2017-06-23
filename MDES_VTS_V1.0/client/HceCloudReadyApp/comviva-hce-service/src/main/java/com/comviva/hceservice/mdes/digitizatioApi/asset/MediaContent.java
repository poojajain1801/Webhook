package com.comviva.hceservice.mdes.digitizatioApi.asset;

/**
 * Media Content
 * Created by tarkeshwar.v on 5/25/2017.
 */
public class MediaContent {
    private AssetType assetType;
    private String data;
    private int height;
    private int width;

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
