package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 5/10/2017.
 */
@Getter
@Setter
public class TDSRegistrationReq {
    private String tokenUniqueReference;
    private String registrationHash;

    public TDSRegistrationReq(String tokenUniqueReference, String registrationHash) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.registrationHash = registrationHash;
    }

    public TDSRegistrationReq(){

    }
}
