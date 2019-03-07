package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * RiskData
 * Created by Amgoth Madan on 5/10/2017.
 */
@Getter
@Setter
public class RiskData {

private String transactionInitiaitionType;
private String acquirerMerchantID;

    public RiskData(String transactionInitiaitionType,String acquirerMerchantID) {
        this.transactionInitiaitionType=transactionInitiaitionType;
        this.acquirerMerchantID=acquirerMerchantID;
    }
    public RiskData() {
    }
}