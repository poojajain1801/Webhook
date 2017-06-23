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

    @Column (name = "VISA_ENABLED")
    private String visaEnabled;

    @Column (name = "MASTERCARD_ENABLED")
    private String mastercardEnabled;

    @Column (name = "VISA_MESSAGE")
    private String visaMessage;

    @Column (name = "MASTERCARD_MESSAGE")
    private String mastercardMessage;

    @Column (name = "DEVICE_STATUS")
    private String deviceStatus;


    public DeviceInfo() {
        //this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public DeviceInfo(String id, String userName, String paymentAppInstanceId, String osName, String osVersion, String nfcCapable,
                      String imei, String clientDeviceId, String vClientId, String deviceModel,
                      String hostDeviceID,String visaEnabled,String mastercardEnabled,String visaMessage,String mastercardMessage,String deviceStatus) {
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
        this.visaEnabled=visaEnabled;
        this.mastercardEnabled=mastercardEnabled;
        this.visaMessage=visaMessage;
        this.mastercardMessage=mastercardMessage;
        this.deviceStatus=deviceStatus;
    }
}