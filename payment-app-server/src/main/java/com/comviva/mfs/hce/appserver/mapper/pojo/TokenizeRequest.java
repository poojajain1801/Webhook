package com.comviva.mfs.hce.appserver.mapper.pojo;

public class TokenizeRequest {

    private String tokenRequestorId;
    private String tokenType;
    private CardInfo cardInfo;
    private String taskId;
    private String paymentAppId;

    public TokenizeRequest(String tokenRequestorId, String tokenType, CardInfo cardInfo, String taskId, String paymentAppId) {
        this.tokenRequestorId = tokenRequestorId;
        this.tokenType = tokenType;
        this.cardInfo = cardInfo;
        this.taskId = taskId;
        this.paymentAppId = paymentAppId;
    }
    public TokenizeRequest() {

    }

    public void setTokenRequestorId(String tokenRequestorId) {
        this.tokenRequestorId = tokenRequestorId;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public String getTokenRequestorId() {
        return tokenRequestorId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getPaymentAppId() {
        return paymentAppId;
    }
}
