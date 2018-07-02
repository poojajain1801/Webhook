package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 19-07-2017.
 */
@Getter
@Setter
public class GetTokensRequest {
    private String paymentAppInstanceId ;
    private String tokenUniqueReference;
    private Boolean includeTokenDetail ;

    public GetTokensRequest(String paymentAppInstanceId ,String tokenUniqueReference , Boolean includeTokenDetail) {
        this.paymentAppInstanceId = paymentAppInstanceId ;
        this.tokenUniqueReference = tokenUniqueReference;
        this.includeTokenDetail = includeTokenDetail ;
    }
    public GetTokensRequest() {
       //This is a default constructor
    }
}
