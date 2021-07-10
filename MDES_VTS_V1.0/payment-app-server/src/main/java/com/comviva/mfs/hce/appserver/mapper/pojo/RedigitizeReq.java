package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedigitizeReq {
    private String paymentAppInstanceId;
    private String cardletId;
    private String tokenUniqueReference;
}
