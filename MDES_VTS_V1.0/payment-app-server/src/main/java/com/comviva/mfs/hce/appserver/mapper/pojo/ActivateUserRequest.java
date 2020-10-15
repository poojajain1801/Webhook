package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ActivateUserRequest.
 * Created by Amgoth.madan on 5/11/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserRequest {
    private String userId;
    private String activationCode;
    private String clientDeviceID;
}