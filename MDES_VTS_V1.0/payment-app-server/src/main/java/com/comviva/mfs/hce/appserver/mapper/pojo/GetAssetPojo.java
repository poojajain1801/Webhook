package com.comviva.mfs.hce.appserver.mapper.pojo;

public class GetAssetPojo {
    private String assetId;

    public GetAssetPojo(String assetId) {
        this.assetId = assetId;
    }
    public GetAssetPojo() {

    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
