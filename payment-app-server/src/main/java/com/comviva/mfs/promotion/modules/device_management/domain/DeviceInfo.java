package com.comviva.mfs.promotion.modules.device_management.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Entity
@Getter
@Table(name = "DEVICE_INFO")
@ToString
@EqualsAndHashCode
public class DeviceInfo {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;


    @Column(name = "OS_NAME")
    private final String osName;

    @Column(name = "OS_VERSION")
    private final String osVersion;

    @Column(name = "SERIAL_NUMBER")
    private final String serialNumber;

    @Column(name = "FORM_FACTOR")
    private final String formFactor;

    @Column(name = "STORAGE_TECHNOLOGY")
    private final String storageTechnology;

    @Column(name = "NFC_CAPABLE")
    private final String nfcCapable;

    @Column(name = "IMEI")
    private final String imei;

    @Column(name = "MSISDN")
    private final String msisdn;

    @Column(name = "DEVICE_NAME")
    private final String deviceName;

    public DeviceInfo(String id, String osName, String osVersion, String serialNumber, String formFactor, String storageTechnology, String nfcCapable, String imei, String msisdn, String devieceName) {
        this.id = id;
        this.osName = osName;
        this.osVersion = osVersion;
        this.serialNumber = serialNumber;
        this.formFactor = formFactor;
        this.storageTechnology = storageTechnology;
        this.nfcCapable = nfcCapable;
        this.imei = imei;
        this.msisdn = msisdn;
        this.deviceName = devieceName;
    }

    public DeviceInfo() {
        this(null, null, null, null, null, null, null, null, null, null);
    }
}
