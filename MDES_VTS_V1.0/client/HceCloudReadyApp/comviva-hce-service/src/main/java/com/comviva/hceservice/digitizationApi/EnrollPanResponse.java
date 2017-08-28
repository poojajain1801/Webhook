package com.comviva.hceservice.digitizationApi;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */

public class EnrollPanResponse {
    private String vPanEnrollmentID;
    private PaymentInstrumentComviva paymentInstrumentComviva;
    private CardMetaData cardMetaData;

    public String getvPanEnrollmentID() {
        return vPanEnrollmentID;
    }

    public void setvPanEnrollmentID(String vPanEnrollmentID) {
        this.vPanEnrollmentID = vPanEnrollmentID;
    }

    public PaymentInstrumentComviva getPaymentInstrumentComviva() {
        return paymentInstrumentComviva;
    }

    public void setPaymentInstrumentComviva(PaymentInstrumentComviva paymentInstrumentComviva) {
        this.paymentInstrumentComviva = paymentInstrumentComviva;
    }

    public CardMetaData getCardMetaData() {
        return cardMetaData;
    }

    public void setCardMetaData(CardMetaData cardMetaData) {
        this.cardMetaData = cardMetaData;
    }
}
