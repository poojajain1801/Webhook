package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Tanmay.Patel on 5/11/2017.
 */
@Getter
@Setter
public class GetTransactionHistoryReq {
    private String tokenUniqueReference;
    private String authenticationCode;
    private String lastUpdatedTag ;

    public GetTransactionHistoryReq(String tokenUniqueReference, String authenticationCode, String lastUpdatedTag) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.authenticationCode = authenticationCode;
        this.lastUpdatedTag = lastUpdatedTag;
    }

    public GetTransactionHistoryReq()
    {
        //This is a default constructor.
    }
}

