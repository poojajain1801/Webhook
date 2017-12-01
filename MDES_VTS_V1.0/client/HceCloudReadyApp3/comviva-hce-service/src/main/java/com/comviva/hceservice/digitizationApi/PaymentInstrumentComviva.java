package com.comviva.hceservice.digitizationApi;

import com.visa.cbp.external.common.ExpirationDate;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */
public class PaymentInstrumentComviva {
    private String last4;
    private Boolean cvv2PrintedInd;
    private Boolean expDatePrintedInd;
    private EnabledServices enabledServices;
    private ExpirationDate expirationDate;

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public Boolean getCvv2PrintedInd() {
        return cvv2PrintedInd;
    }

    public void setCvv2PrintedInd(Boolean cvv2PrintedInd) {
        this.cvv2PrintedInd = cvv2PrintedInd;
    }

    public Boolean getExpDatePrintedInd() {
        return expDatePrintedInd;
    }

    public void setExpDatePrintedInd(Boolean expDatePrintedInd) {
        this.expDatePrintedInd = expDatePrintedInd;
    }

    public EnabledServices getEnabledServices() {
        return enabledServices;
    }

    public void setEnabledServices(EnabledServices enabledServices) {
        this.enabledServices = enabledServices;
    }

    public ExpirationDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(ExpirationDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
