package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Activate request
 * Created by tarkeshwar.v on 2/10/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivateReq {
    private String paymentAppInstanceId;
    private String tokenUniqueReference;
    private String authenticationCode;
    private String tokenizationAuthenticationValue;
}
