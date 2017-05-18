package com.comviva.mfs.hce.appserver.mapper.pojo;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Object to contain all PaymentInstrument details.
 * Created by amgoth.naik on 4/25/2017.
 */
@Getter
@Setter
public class PaymentInstrument {

    private String last4;
    private String expirationDate;
    private String indicators;
    private String expDatePrintedInd;
    private String cvv2;
    private String PrintedInd;
    private String paymentAccountReference;

    public PaymentInstrument(String last4,
                             String expirationDate,
                             String indicators,
                             String expDatePrintedInd,
                             String cvv2,
                             String PrintedInd,
                             String paymentAccountReference) {
        this.last4 = last4;
        this.expirationDate = expirationDate;
        this.indicators = indicators;
        this.expDatePrintedInd = expDatePrintedInd;
        this.cvv2 = cvv2;
        this.PrintedInd= PrintedInd;
        this.paymentAccountReference = paymentAccountReference;
    }

    public PaymentInstrument() {
    }
}