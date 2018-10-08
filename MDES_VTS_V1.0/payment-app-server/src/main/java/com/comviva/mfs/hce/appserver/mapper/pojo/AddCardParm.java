package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by tanmay.patel on 1/31/2017.
 */

public class AddCardParm extends PayAppServerReq{

    private String tokenType;
    private String paymentAppInstanceId;
    private String paymentAppId;
    private CardInfo cardInfo;
    private DeviceInfoRequest deviceInfo;
    private String cardletId;
    private String consumerLanguage;
    private String tokenAuthenticationValue;
    private String decisioningData;


    public AddCardParm(String serviceId, String tokenType, String paymentAppInstanceId, String paymentAppId, CardInfo cardInfo,DeviceInfoRequest deviceInfo, String cardletId, String consumerLanguage, String tokenAuthenticationValue, String decisioningData) {
        super(serviceId);
        this.tokenType = tokenType;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.paymentAppId = paymentAppId;
        this.cardInfo = cardInfo;
        this.deviceInfo = deviceInfo;
        this.cardletId = cardletId;
        this.consumerLanguage = consumerLanguage;
        this.tokenAuthenticationValue = tokenAuthenticationValue;
        this.decisioningData = decisioningData;
    }


    public AddCardParm() {
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public String getPaymentAppId() {
        return paymentAppId;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public String getCardletId() {
        return cardletId;
    }

    public String getConsumerLanguage() {
        return consumerLanguage;
    }

    public String getTokenAuthenticationValue() {
        return tokenAuthenticationValue;
    }

    public String getDecisioningData() {
        return decisioningData;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public void setCardletId(String cardletId) {
        this.cardletId = cardletId;
    }

    public void setConsumerLanguage(String consumerLanguage) {
        this.consumerLanguage = consumerLanguage;
    }

    public void setTokenAuthenticationValue(String tokenAuthenticationValue) {
        this.tokenAuthenticationValue = tokenAuthenticationValue;
    }

    public void setDecisioningData(String decisioningData) {
        this.decisioningData = decisioningData;
    }

    public DeviceInfoRequest getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoRequest deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
