package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * EnrollPan Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class EncPaymentInstrument {
    private String accountNumber;
    private String cvv2;
    private String  name;
    private ExpirationDate expirationDate;
    private BillingAddress billingAddress;
    private InstrumentProvider provider;

    public EncPaymentInstrument(String accountNumber, String cvv2, String name, ExpirationDate expirationDate, BillingAddress billingAddress, InstrumentProvider provider) {
        this.accountNumber = accountNumber;
        this.cvv2 = cvv2;
        this.name = name;
        this.expirationDate = expirationDate;
        this.billingAddress = billingAddress;
        this.provider = provider;
    }

    public EncPaymentInstrument() {
    }
}