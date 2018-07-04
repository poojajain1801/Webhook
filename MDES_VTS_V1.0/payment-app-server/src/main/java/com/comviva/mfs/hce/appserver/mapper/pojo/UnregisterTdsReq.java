package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 14-03-2018.
 */

@Getter
@Setter
public class UnregisterTdsReq {
    private String tokenUniqueReference ;
    private String authenticationCode ;

    public UnregisterTdsReq(String tokenUniqueReference, String authenticationCode) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.authenticationCode = authenticationCode;
    }

    public UnregisterTdsReq() {

    }
}
