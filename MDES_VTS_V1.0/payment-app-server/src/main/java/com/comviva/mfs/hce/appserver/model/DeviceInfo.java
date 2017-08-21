package com.comviva.mfs.hce.appserver.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;



/**
 * The persistent class for the DEVICE_INFO database table.
 *
 */
@Entity
@Table(name="DEVICE_INFO")
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name="CLIENT_DEVICE_ID")
    private String clientDeviceId;

    @Column(name="DEVICE_MODEL")
    private String deviceModel;

    @Column(name="DEVICE_STATUS")
    private String deviceStatus;

    @Column(name="HOST_DEVICE_ID")
    private String hostDeviceId;

    private String imei;

    @Column(name="MASTERCARD_ENABLED")
    private String mastercardEnabled;

    @Column(name="MASTERCARD_MESSAGE")
    private String mastercardMessage;

    @Column(name="NFC_CAPABLE")
    private String nfcCapable;

    @Column(name="OS_NAME")
    private String osName;

    @Column(name="OS_VERSION")
    private String osVersion;

    @Column(name="PAYMENT_APP_ID")
    private String paymentAppId;

    @Column(name="PAYMENT_APP_INSTANCE_ID")
    private String paymentAppInstanceId;

    @Column(name="RNS_REGISTRATION_ID")
    private String rnsRegistrationId;

    @Column(name="V_CLIENT_ID")
    private String vClientId;

    @Column(name="VISA_ENABLED")
    private String visaEnabled;

    @Column(name="VISA_MESSAGE")
    private String visaMessage;

    @Column(name="VSERVER_NONCE")
    private String vserverNonce;

    public DeviceInfo() {
    }


    public DeviceInfo(String id,String paymentAppInstanceId, String paymentAppId,String rnsId, String osName, String osVersion, String nfcCapable,
                      String imei, String clientDeviceId, String vClientId, String deviceModel,
                      String hostDeviceID,String visaEnabled,String mastercardEnabled,String visaMessage,String mastercardMessage,String deviceStatus,
                      String vtscerts_vcertificateid_confidentiality,String vtscerts_certusage_confidentiality,String vtscerts_vcertificateid_integrity,
                      String vtscerts_certusage_integrity, String devicecerts_certvalue_confidentiality, String devicecerts_certusage_confidentiality,
                      String devicecerts_certformat_confidentiality, String devicecerts_certvalue_integrity,    String devicecerts_certusage_integrity,
                      String devicecerts_certformat_integrity,String vserver_nonce) {
        this.id = id;
        // this.userName=userName;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.paymentAppId=paymentAppId;
        this.rnsRegistrationId = rnsId;
        this.osName = osName;
        this.osVersion = osVersion;
        this.nfcCapable = nfcCapable;
        this.imei = imei;
        this.clientDeviceId = clientDeviceId;
        this.vClientId = vClientId;
        this.deviceModel = deviceModel;
        this.hostDeviceId = hostDeviceID;
        this.visaEnabled=visaEnabled;
        this.mastercardEnabled=mastercardEnabled;
        this.visaMessage=visaMessage;
        this.mastercardMessage=mastercardMessage;
        this.deviceStatus=deviceStatus;

        this.vserverNonce=vserver_nonce;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientDeviceId() {
        return this.clientDeviceId;
    }

    public void setClientDeviceId(String clientDeviceId) {
        this.clientDeviceId = clientDeviceId;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getHostDeviceId() {
        return this.hostDeviceId;
    }

    public void setHostDeviceId(String hostDeviceId) {
        this.hostDeviceId = hostDeviceId;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMastercardEnabled() {
        return this.mastercardEnabled;
    }

    public void setMastercardEnabled(String mastercardEnabled) {
        this.mastercardEnabled = mastercardEnabled;
    }

    public String getMastercardMessage() {
        return this.mastercardMessage;
    }

    public void setMastercardMessage(String mastercardMessage) {
        this.mastercardMessage = mastercardMessage;
    }

    public String getNfcCapable() {
        return this.nfcCapable;
    }

    public void setNfcCapable(String nfcCapable) {
        this.nfcCapable = nfcCapable;
    }

    public String getOsName() {
        return this.osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPaymentAppId() {
        return this.paymentAppId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public String getPaymentAppInstanceId() {
        return this.paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getRnsRegistrationId() {
        return this.rnsRegistrationId;
    }

    public void setRnsRegistrationId(String rnsRegistrationId) {
        this.rnsRegistrationId = rnsRegistrationId;
    }

    public String getVClientId() {
        return this.vClientId;
    }

    public void setVClientId(String vClientId) {
        this.vClientId = vClientId;
    }

    public String getVisaEnabled() {
        return this.visaEnabled;
    }

    public void setVisaEnabled(String visaEnabled) {
        this.visaEnabled = visaEnabled;
    }

    public String getVisaMessage() {
        return this.visaMessage;
    }

    public void setVisaMessage(String visaMessage) {
        this.visaMessage = visaMessage;
    }

    public String getVserverNonce() {
        return this.vserverNonce;
    }

    public void setVserverNonce(String vserverNonce) {
        this.vserverNonce = vserverNonce;
    }

}