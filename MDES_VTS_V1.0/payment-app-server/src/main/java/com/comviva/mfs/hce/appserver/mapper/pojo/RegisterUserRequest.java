package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * RegisterUserRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class RegisterUserRequest {

    private String userId;


    public RegisterUserRequest(String userId) {

        this.userId=userId;
    }

    public RegisterUserRequest() {
    }
}