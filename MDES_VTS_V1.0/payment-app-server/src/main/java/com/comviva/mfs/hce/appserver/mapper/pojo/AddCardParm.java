package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by tanmay.patel on 1/31/2017.
 */
@Getter
public class AddCardParm extends PayAppServerReq{

    private String tokenType;
    private String paymentAppInstanceId;
    private String paymentAppId;
    private CardInfo cardInfo;
    private String cardId;
    private String consumerLanguage;
    private String tokenAuthenticationValue;
    private String decisioningData;

    public AddCardParm(String serviceId, String tokenType, String paymentAppInstanceId, String paymentAppId, CardInfo cardInfo, String cardId, String consumerLanguage, String tokenAuthenticationValue, String decisioningData) {
        super(serviceId);
        this.tokenType = tokenType;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.paymentAppId = paymentAppId;
        this.cardInfo = cardInfo;
        this.cardId = cardId;
        this.consumerLanguage = consumerLanguage;
        this.tokenAuthenticationValue = tokenAuthenticationValue;
        this.decisioningData = decisioningData;
    }


    public AddCardParm() {
    }
}
