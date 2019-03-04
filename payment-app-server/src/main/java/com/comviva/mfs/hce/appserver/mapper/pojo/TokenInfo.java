package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Amgoth.madan on 1/8/2017.
 */
@Getter
@ToString
@EqualsAndHashCode
public class TokenInfo {

    private String  tokenStatus;
    private String  tokenRequestorID;
    private String  tokenReferenceID;
    private String  last4;
    private ExpirationDate expirationDate;
    private String appPrgrmID;
    private String encTokenInfo;
    private String hceData;
    private String mst;
    private String seCardPerso;

    public TokenInfo(String tokenStatus,String tokenRequestorID,String tokenReferenceID,String last4,ExpirationDate expirationDate,String appPrgrmID,
                     String encTokenInfo,String hceData,String mst,String seCardPerso)
    {
        this.tokenStatus=tokenStatus;
        this.tokenRequestorID=tokenRequestorID;
        this.tokenReferenceID=tokenReferenceID;
        this.last4=last4;
        this.expirationDate=expirationDate;
        this.appPrgrmID=appPrgrmID;
        this.encTokenInfo=encTokenInfo;
        this.hceData=hceData;
        this.mst=mst;
        this.seCardPerso=seCardPerso;
    }
}
