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
    private final Map<String, Object> response;

    public UserRegistrationResponse(Map<String, Object> response) {
        this.response = response;
    }
}
