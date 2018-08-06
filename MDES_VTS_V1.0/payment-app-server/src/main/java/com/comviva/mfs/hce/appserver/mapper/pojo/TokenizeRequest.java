package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
