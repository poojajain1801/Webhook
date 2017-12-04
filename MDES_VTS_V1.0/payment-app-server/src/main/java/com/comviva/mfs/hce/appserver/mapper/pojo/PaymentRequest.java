package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * PaymentRequest
 * Created by madan.amgoth on 5/10/2017.
 */
@Getter
@Setter
public class PaymentRequest {
    private String transactionType;
    private String atc;

    public PaymentRequest(String transactionType, String atc) {
        this.transactionType = transactionType;
        this.atc = atc;
    }
    public PaymentRequest() {
    }
}
