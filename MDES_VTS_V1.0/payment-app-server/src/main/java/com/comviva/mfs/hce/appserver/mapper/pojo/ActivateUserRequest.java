package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.*;

/**
 * ActivateUserRequest.
 * Created by Amgoth.madan on 5/11/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivateUserRequest {
    private String userId;
    private String activationCode;
    private String clientDeviceID;
}