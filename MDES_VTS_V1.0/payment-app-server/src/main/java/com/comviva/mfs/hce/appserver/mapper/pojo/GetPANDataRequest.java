package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * DeleteTokenRequest.
 * Created by Amgoth.madan on 5/10/2017.
 */
@Getter
@Setter
public class GetPANDataRequest {

    private String userId;
    private String activationCode;
    private String encPaymentInstrument;


    public GetPANDataRequest(String userId, String activationCode, String encPaymentInstrument) {
        this.userId = userId;
        this.activationCode = activationCode;
        this.encPaymentInstrument = encPaymentInstrument;
    }

    public GetPANDataRequest() {
    }
}