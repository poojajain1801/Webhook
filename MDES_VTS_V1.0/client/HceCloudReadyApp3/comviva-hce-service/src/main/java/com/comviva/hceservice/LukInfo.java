package com.comviva.hceservice;


import java.util.Date;

public class LukInfo {
    private String cardUniqueId;
    private int noOfPaymentsRemaining;
    private Date keyExpTime;

    public String getCardUniqueId() {
        return cardUniqueId;
    }

    public void setCard(String cardUniqueId) {
        this.cardUniqueId = cardUniqueId;
    }

    public int getNoOfPaymentsRemaining() {
        return noOfPaymentsRemaining;
    }

    public void setNoOfPaymentsRemaining(int noOfPaymentsRemaining) {
        this.noOfPaymentsRemaining = noOfPaymentsRemaining;
    }

    public Date getKeyExpTime() {
        return keyExpTime;
    }

    public void setKeyExpTime(Date keyExpTime) {
        this.keyExpTime = keyExpTime;
    }
}
