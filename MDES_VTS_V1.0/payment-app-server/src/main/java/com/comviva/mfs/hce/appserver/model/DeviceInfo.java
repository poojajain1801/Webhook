package com.comviva.mfs.hce.appserver.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the DEVICE_INFO database table.
 *
 */
@Entity
@Table(name="DEVICE_INFO")
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="CLIENT_DEVICE_ID")
    private String clientDeviceId;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="DEVICE_MODEL")
    private String deviceModel;

    @Column(name="HOST_DEVICE_ID")
    private String hostDeviceId;

    private String imei;

    @Column(name="IS_MASTERCARD_ENABLED")
    private String isMastercardEnabled;

    @Column(name="IS_VISA_ENABLED")
    private String isVisaEnabled;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;

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

    private String status;

    @Column(name="V_CLIENT_ID")
    private String vClientId;

    //bi-directional many-to-one association to UserDetail
    @ManyToOne
    @JoinColumn(name="CLIENT_WALLET_ACCOUNT_ID")
    private UserDetail userDetail;

    public DeviceInfo() {
    }

    public DeviceInfo(String clientDeviceId, Timestamp createdOn, String deviceModel, String hostDeviceId, String imei, String isMastercardEnabled, String isVisaEnabled, Timestamp modifiedOn, String nfcCapable, String osName, String osVersion, String paymentAppId, String paymentAppInstanceId, String rnsRegistrationId, String status, String vClientId, UserDetail userDetail) {
        this.clientDeviceId = clientDeviceId;
        this.createdOn = createdOn;
        this.deviceModel = deviceModel;
        this.hostDeviceId = hostDeviceId;
        this.imei = imei;
        this.isMastercardEnabled = isMastercardEnabled;
        this.isVisaEnabled = isVisaEnabled;
        this.modifiedOn = modifiedOn;
        this.nfcCapable = nfcCapable;
        this.osName = osName;
        this.osVersion = osVersion;
        this.paymentAppId = paymentAppId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.rnsRegistrationId = rnsRegistrationId;
        this.status = status;
        this.vClientId = vClientId;
        this.userDetail = userDetail;
    }

    public String getClientDeviceId() {
        return this.clientDeviceId;
    }

    public void setClientDeviceId(String clientDeviceId) {
        this.clientDeviceId = clientDeviceId;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
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

    public String getIsMastercardEnabled() {
        return this.isMastercardEnabled;
    }

    public void setIsMastercardEnabled(String isMastercardEnabled) {
        this.isMastercardEnabled = isMastercardEnabled;
    }

    public String getIsVisaEnabled() {
        return this.isVisaEnabled;
    }

    public void setIsVisaEnabled(String isVisaEnabled) {
        this.isVisaEnabled = isVisaEnabled;
    }

    public Timestamp getModifiedOn() {
        return this.modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVClientId() {
        return this.vClientId;
    }

    public void setVClientId(String vClientId) {
        this.vClientId = vClientId;
    }

    public UserDetail getUserDetail() {
        return this.userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

}