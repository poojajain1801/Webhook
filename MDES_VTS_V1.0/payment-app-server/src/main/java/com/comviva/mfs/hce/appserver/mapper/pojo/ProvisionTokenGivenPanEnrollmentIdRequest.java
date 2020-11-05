package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProvisionTokenGivenPanEnrollmentId Request
 * Created by amgoth madan on 4/25/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvisionTokenGivenPanEnrollmentIdRequest {

private String clientAppId;
private String clientWalletAccountId;
private String emailAddress;
private String protectionType;
private String presentationType;
private String panEnrollmentID;
private String clientDeviceID;
private String termsAndConditionsID;

/*
    public ProvisionTokenGivenPanEnrollmentIdRequest(String clientAppId, String clientWalletAccountId, String emailAddress, String protectionType, String presentationType, String panEnrollmentID, String clientDeviceID, String termsAndConditionsID) {
        this.clientAppId = clientAppId;
        this.clientWalletAccountId = clientWalletAccountId;
        this.emailAddress = emailAddress;
        this.protectionType = protectionType;
        this.presentationType = presentationType;
        this.panEnrollmentID = panEnrollmentID;
        this.clientDeviceID = clientDeviceID;
        this.termsAndConditionsID = termsAndConditionsID;
    }

    public ProvisionTokenGivenPanEnrollmentIdRequest() {
    } */
}