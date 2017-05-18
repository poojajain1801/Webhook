package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ActivateUserRequest.
 * Created by Amgoth.madan on 5/11/2017.
 */
@Getter
@Setter
public class ActivateUserRequest {

    private String userId;
    private String activationCode;

    public ActivateUserRequest(String userId, String activationCode) {

        this.userId=userId;
        this.activationCode=activationCode;
    }

    public ActivateUserRequest() {
    }
}