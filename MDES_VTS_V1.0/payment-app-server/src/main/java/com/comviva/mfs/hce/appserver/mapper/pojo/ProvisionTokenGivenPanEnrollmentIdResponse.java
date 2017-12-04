package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ProvisionTokenGivenPanEnrollmentId Response
 * Created by amgoth madan on 4/25/2017.
 */
@Getter
@Setter
public class ProvisionTokenGivenPanEnrollmentIdResponse {

    private String vProvisionedTokenID;
    private PaymentInstrument paymentInstrument;
    private TokenInfo tokenInfo;
    private StepUpRequest stepUpRequest;
    private String encryptionMetaData;
    private ODAdata oDAdata;


    public ProvisionTokenGivenPanEnrollmentIdResponse(String vProvisionedTokenID,PaymentInstrument paymentInstrument,TokenInfo tokenInfo,StepUpRequest stepUpRequest,
                                                      String encryptionMetaData,ODAdata oDAdata) {

        this.vProvisionedTokenID=vProvisionedTokenID;
        this.paymentInstrument=paymentInstrument;
        this.tokenInfo=tokenInfo;
        this.stepUpRequest=stepUpRequest;
        this.encryptionMetaData=encryptionMetaData;
        this.oDAdata=oDAdata;
    }

    public ProvisionTokenGivenPanEnrollmentIdResponse() {
    }
}