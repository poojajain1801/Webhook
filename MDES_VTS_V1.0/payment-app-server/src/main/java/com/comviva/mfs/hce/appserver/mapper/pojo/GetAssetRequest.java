package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Response for Get GetAssetRequest API.
 * Created by tarkeshwar.v on 2/8/2017.
 */
@Getter
@ToString
@EqualsAndHashCode
@Setter
public class GetAssetRequest {
    private String assetId;

    public GetAssetRequest(String assetId) {
        this.assetId = assetId;
    }

    public GetAssetRequest(){

    }
}
