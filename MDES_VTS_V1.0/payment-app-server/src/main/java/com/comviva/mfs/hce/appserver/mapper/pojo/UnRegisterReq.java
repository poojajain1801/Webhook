package com.comviva.mfs.hce.appserver.mapper.pojo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnRegisterReq {
    private String imei;
    private String userId;
    private String paymentAppInstanceId;
}
