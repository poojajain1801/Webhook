package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.digitizationApi.asset.AssetType;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */

public class Content {
    int width;
    int height;

    public Content(int width, int height, AssetType mimeType) {
        this.width = width;
        this.height = height;
        this.mimeType = mimeType;
    }

    private AssetType mimeType;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public AssetType getMimeType() {
        return mimeType;
    }

    public void setMimeType(AssetType mimeType) {
        this.mimeType = mimeType;
    }
}
