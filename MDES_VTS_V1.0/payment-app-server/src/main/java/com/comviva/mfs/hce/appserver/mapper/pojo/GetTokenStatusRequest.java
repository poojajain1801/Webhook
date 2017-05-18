package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by amgoth.madan on 5/10/2017.
 */
@Getter
public class GetTokenStatusRequest extends PayAppServerReq{

    private String userId;
    private String activationCode;

    public GetTokenStatusRequest(String userId, String activationCode) {
        this.userId = userId;
        this.activationCode = activationCode;
    }

    public GetTokenStatusRequest() {
    }
}
