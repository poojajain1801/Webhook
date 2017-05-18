package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * GetTransactionHistoryRequest.
 * Created by Amgoth.madan on 5/10/2017.
 */
@Getter
@Setter
public class GetTransactionHistoryRequest {

    private String userId;
    private String activationCode;

    public GetTransactionHistoryRequest(String userId, String activationCode) {

        this.userId=userId;
        this.activationCode=activationCode;
    }

    public GetTransactionHistoryRequest() {
    }
}