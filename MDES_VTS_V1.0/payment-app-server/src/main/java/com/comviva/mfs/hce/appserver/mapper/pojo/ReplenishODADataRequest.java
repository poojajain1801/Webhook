package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ReplenishODADataRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ReplenishODADataRequest {

    private String userId;
    private String activationCode;

    public ReplenishODADataRequest(String userId, String activationCode) {

        this.userId=userId;
        this.activationCode=activationCode;
    }

    public ReplenishODADataRequest() {
    }
}