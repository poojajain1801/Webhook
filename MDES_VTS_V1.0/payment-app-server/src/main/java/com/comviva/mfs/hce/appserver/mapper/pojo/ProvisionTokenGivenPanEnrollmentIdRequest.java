package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ProvisionTokenGivenPanEnrollmentId Request
 * Created by amgoth madan on 4/25/2017.
 */
@Getter
@Setter
public class ProvisionTokenGivenPanEnrollmentIdRequest {

    private String userId;
    private String activationCode;
    private String vPanEnrollmentID;
    private String encryptionMetaData;
    private String clientAppID;
    private String clientDeviceID;
    private String clientWalletAccountID;
    private String ip4address;
    private String location;
    private String locationSource;
    private String issuerAuthCode;
    private String emailAddressHash;
    private String emailAddress;
    private String protectionType;
    private String presentationType;
    private TermsAndConditions termsAndConditions;
    private String accountType;
    private String encRiskDataInfo;
    private SsdData ssdData;
    private String channelSecurityContext;
    private String platformType;


    public ProvisionTokenGivenPanEnrollmentIdRequest(String userId, String activationCode, String vPanEnrollmentID,String encryptionMetaData,String clientAppID,String clientDeviceID,
                                                     String clientWalletAccountID,String ip4address,String location,String locationSource,String issuerAuthCode,String emailAddressHash,
                                                     String emailAddress,String protectionType,String presentationType,TermsAndConditions termsAndConditions,String accountType, String encRiskDataInfo,
                                                       SsdData ssdData, String channelSecurityContext,String platformType) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.vPanEnrollmentID=vPanEnrollmentID;
        this.encryptionMetaData=encryptionMetaData;
        this.clientAppID=clientAppID;
        this.clientDeviceID=clientDeviceID;
        this.clientWalletAccountID=clientWalletAccountID;
        this.ip4address=ip4address;
        this.location=location;
        this.locationSource=locationSource;
        this.issuerAuthCode=issuerAuthCode;
        this.emailAddressHash=emailAddressHash;
        this.emailAddress=emailAddress;
        this.protectionType=protectionType;
        this.presentationType=presentationType;
        this.termsAndConditions=termsAndConditions;
        this.accountType=accountType;
        this.encRiskDataInfo=encRiskDataInfo;
        this.ssdData=ssdData;
        this.channelSecurityContext=channelSecurityContext;
        this.platformType=platformType;
    }

    public ProvisionTokenGivenPanEnrollmentIdRequest() {
    }
}