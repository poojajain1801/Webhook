package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 5/9/2017.
 */
@Getter
@Setter
public class GetRegistrationCodeReq {
    private String tokenUniqueReference;

    public GetRegistrationCodeReq(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    public GetRegistrationCodeReq()
    {
        
    }
}
