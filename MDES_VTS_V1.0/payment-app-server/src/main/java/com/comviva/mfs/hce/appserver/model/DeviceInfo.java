package com.comviva.mfs.hce.appserver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "DEVICE_INFO")
@ToString
@EqualsAndHashCode
public class DeviceInfo {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstanceId;

    /**
     * -- Device information common to both VTS & MDES
     */
    // For VTS must be sent as osType
    @Column(name = "OS_NAME")
    private String osName;

    @Column(name = "OS_VERSION")
    private String osVersion;


    @Column(name = "NFC_CAPABLE")
    private String nfcCapable;

    @Column(name = "IMEI")
    private String imei;



    /**
     * -- Device information specific to VTS
     */
    @Column(name = "CLIENT_DEVICE_ID")
    private String clientDeviceId;

    @Column(name = "V_CLIENT_ID")
    private String vClientId;

    @Column(name = "DEVICE_MODEL")
    private String deviceModel;

    @Column(name = "HOST_DEVICE_ID")
    private String hostDeviceID;




    public DeviceInfo() {
        //this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /** Prepares device information in JSON format for MDES.
     * @return  Device Informtaion in JSON format
     */
  /*  public JSONObject getDeviceInfoForMdes() {
        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("osName", osName);
        deviceInfo.put("osVersion", osVersion);
        deviceInfo.put("formFactor", formFactor);
        deviceInfo.put("deviceName", deviceName);
        deviceInfo.put("id", serialNumber);
        deviceInfo.put("imei", imei);
        deviceInfo.put("msisdn", msisdn);
        deviceInfo.put("nfcCapable", nfcCapable);
        deviceInfo.put("serialNumber", serialNumber);
        deviceInfo.put("storageTechnology", storageTechnology);
        return deviceInfo;
    }

    *//**
     * Prepares device information in JSON format for VTS.
     * @return Device Informtaion in JSON format
     *//*
    public JSONObject getDeviceInfoForVts() {
        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("osType", osName);
        deviceInfo.put("osVersion", osVersion);
        deviceInfo.put("deviceType", deviceType);
        deviceInfo.put("deviceName", deviceNickName);
        deviceInfo.put("deviceManufacturer", deviceManufacturer);
        deviceInfo.put("deviceModel", deviceModel);
        deviceInfo.put("hostDeviceID", hostDeviceID);
        deviceInfo.put("phoneNumber", msisdn);
        deviceInfo.put("osBuildID", osBuildID);
        deviceInfo.put("deviceIDType", deviceIDType);
        return deviceInfo;
    }*/

    public DeviceInfo(String id, String userName, String paymentAppInstanceId, String osName, String osVersion, String nfcCapable, String imei, String clientDeviceId, String vClientId, String deviceModel, String hostDeviceID) {
        this.id = id;
        this.userName = userName;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.osName = osName;
        this.osVersion = osVersion;
        this.nfcCapable = nfcCapable;
        this.imei = imei;
        this.clientDeviceId = clientDeviceId;
        this.vClientId = vClientId;
        this.deviceModel = deviceModel;
        this.hostDeviceID = hostDeviceID;
    }
}
