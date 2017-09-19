package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 19-07-2017.
 */
@Getter
public class GetTokensRequest {
    private String tokenUniqueReference;

    public GetTokensRequest(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }
    public GetTokensRequest() {
       //This is a default constructor
    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }
}
