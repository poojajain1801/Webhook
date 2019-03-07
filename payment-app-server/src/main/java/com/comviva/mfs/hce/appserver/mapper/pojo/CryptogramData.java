package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * CryptogramData
 * Created by Amgoth Madan on 5/10/2017.
 */
@Getter
@Setter
public class CryptogramData {

private String cryptogram;
private String eci;
    public CryptogramData(String cryptogram,String eci) {

        this.cryptogram=cryptogram;
        this.eci=eci;
    }
    public CryptogramData() {
    }
}