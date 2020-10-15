package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenizeRequest {
    private String tokenRequestorId;
    private String tokenType;
    private CardInfo cardInfo;
    private String taskId;
    private String paymentAppId;
}
