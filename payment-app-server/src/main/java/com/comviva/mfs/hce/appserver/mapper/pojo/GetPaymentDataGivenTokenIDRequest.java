package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * GetPaymentDataGivenTokenIDRequest.
 * Created by Amgoth.madan on 5/9/2017.
 */
@Getter
@Setter
public class GetPaymentDataGivenTokenIDRequest {

    private String userId;
    private String activationCode;
    private String vProvisionedTokenID;
    private EncryptionMetaData encryptionMetaData;
    private String clientPaymentdataID;
    private PaymentRequest paymentRequest;
    private String atc;
    private ThreeDsData threeDsData;
    private String cryptogramType;
    private RiskData riskData;


    public GetPaymentDataGivenTokenIDRequest(String userId, String activationCode,String vProvisionedTokenID,EncryptionMetaData encryptionMetaData,String clientPaymentdataID,
                                             PaymentRequest paymentRequest,String atc,ThreeDsData threeDsData,String cryptogramType,RiskData riskData) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.vProvisionedTokenID=vProvisionedTokenID;
        this.encryptionMetaData=encryptionMetaData;
        this.clientPaymentdataID=clientPaymentdataID;
        this.paymentRequest=paymentRequest;
        this.atc=atc;
        this.threeDsData=threeDsData;
        this.cryptogramType=cryptogramType;
        this.riskData=riskData;
    }

    public GetPaymentDataGivenTokenIDRequest() {
    }
}