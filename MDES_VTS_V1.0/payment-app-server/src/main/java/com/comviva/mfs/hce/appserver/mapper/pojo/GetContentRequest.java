package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by amgoth.madan on 5/10/2017.
 */
@Getter
public class GetContentRequest extends PayAppServerReq{

    private String userId;
    private String activationCode;

    public GetContentRequest(String userId, String activationCode) {
        this.userId = userId;
        this.activationCode = activationCode;
    }

    public GetContentRequest() {
    }
}
