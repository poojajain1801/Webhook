package com.comviva.mfs.hce.appserver.mapper.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


/**
 * Hvt Management Request
 *
 * */
@Data
public class HvtManagementRequest {
    @JsonIgnore
    private String paymentAppId;
    private String isHvtSupported;
    private String hvtLimit;
    private String themeColor;
    private String transactionTime;
}
