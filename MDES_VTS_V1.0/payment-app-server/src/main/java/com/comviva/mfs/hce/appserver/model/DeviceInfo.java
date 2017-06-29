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


  /*  @Column(name = "USER_NAME")
    private String userName;*/

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstanceId;

    @Column(name = "payment_app_id")
    private String paymentAppId;
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

    @Column (name = "vtscerts_vcertificateid_confidentiality")
    private String vtscerts_vcertificateid_confidentiality;

    @Column(name = "vtscerts_certusage_confidentiality")
    private String vtscerts_certusage_confidentiality;

    @Column(name = "vtscerts_vcertificateid_integrity")
    private String vtscerts_vcertificateid_integrity;

    @Column(name = "vtscerts_certusage_integrity")
    private String vtscerts_certusage_integrity;

    @Column(name = "devicecerts_certvalue_confidentiality")
    private String devicecerts_certvalue_confidentiality;

    @Column(name = "devicecerts_certusage_confidentiality")
    private String devicecerts_certusage_confidentiality;

    @Column(name = "devicecerts_certformat_confidentiality")
    private String devicecerts_certformat_confidentiality;

    @Column(name = "devicecerts_certvalue_integrity")
    private String devicecerts_certvalue_integrity;

    @Column(name = "devicecerts_certusage_integrity")
    private String devicecerts_certusage_integrity;

    @Column(name = "devicecerts_certformat_integrity")
    private String devicecerts_certformat_integrity;

    @Column(name = "vserver_nonce")
    private String vserver_nonce;

    public DeviceInfo() {
        //this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public DeviceInfo(String id,String paymentAppInstanceId, String paymentAppId,String osName, String osVersion, String nfcCapable,
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
        this.vtscerts_vcertificateid_confidentiality=vtscerts_vcertificateid_confidentiality;
        this.vtscerts_certusage_confidentiality=vtscerts_certusage_confidentiality;
        this.vtscerts_vcertificateid_integrity=vtscerts_vcertificateid_integrity;
        this.vtscerts_certusage_integrity=vtscerts_certusage_integrity;
        this.devicecerts_certvalue_confidentiality=devicecerts_certvalue_confidentiality;
        this.devicecerts_certusage_confidentiality=devicecerts_certusage_confidentiality;
        this.devicecerts_certformat_confidentiality=devicecerts_certformat_confidentiality;
        this.devicecerts_certvalue_integrity=devicecerts_certvalue_integrity;
        this.devicecerts_certusage_integrity=devicecerts_certusage_integrity;
        this.devicecerts_certformat_integrity=devicecerts_certformat_integrity;
        this.vserver_nonce=vserver_nonce;
    }
}