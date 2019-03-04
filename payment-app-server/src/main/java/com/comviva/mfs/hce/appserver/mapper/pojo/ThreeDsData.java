package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ThreeDsData
 * Created by Amgoth Madan on 5/10/2017.
 */
@Getter
@Setter
public class ThreeDsData {

private CryptogramData cryptogramData;
    public ThreeDsData(CryptogramData cryptogramData) {
        this.cryptogramData=cryptogramData;
    }
    public ThreeDsData() {
    }
}