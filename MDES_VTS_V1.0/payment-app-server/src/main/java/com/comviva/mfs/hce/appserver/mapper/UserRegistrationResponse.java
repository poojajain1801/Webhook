package com.comviva.mfs.hce.appserver.mapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Getter
@ToString
@EqualsAndHashCode
public class UserRegistrationResponse {
    private final Map response;

    public UserRegistrationResponse(Map response) {
        this.response = response;
    }
}
