package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * GetCardMetadataRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class GetCardMetadataRequest {

    private String userId;
    private String activationCode;
    public GetCardMetadataRequest(String userId, String activationCode) {

        this.userId=userId;
        this.activationCode=activationCode;
    }

    public GetCardMetadataRequest() {
    }
}