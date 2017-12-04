package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * EncryptionMetaData
 * Created by Madan amgoth on  5/10/2017.
 */
@Getter
@Setter
public class EncryptionMetaData {
    private PaymentRequest paymentRequest;

    public EncryptionMetaData(PaymentRequest paymentRequest) {
        this.paymentRequest=paymentRequest;
    }

    public EncryptionMetaData() {
    }
}
