package com.comviva.mfs.promotion.modules.mpamanagement.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "APPLICATION_INSTANCE_INFO")
@ToString
@EqualsAndHashCode
public class ApplicationInstanceInfo {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

    @Column(name = "payment_app_id")
    private final String paymentAppId;

    @Column(name = "payment_app_instance_id")
    private final String paymentAppInstId;

    @Column(name = "device_fingerprint")
    private final String deviceFingerprint;

    @Column(name = "mobile_pin")
    private final String mobilePin;

    @Column(name = "mobile_keyset_id")
    private final String mobileKeySetId;

    @Column(name = "transport_key")
    private final String transportKey;

    @Column(name = "mac_key")
    private final String macKey;

    @Column(name = "data_encryption_key")
    private final String dataEncryptionKey;

    @Column(name = "rns_registration_id")
    private final String rnsRegistrationId;

    public ApplicationInstanceInfo(String id,
                                   String paymentAppId,
                                   String paymentAppInstId,
                                   String deviceFingerprint,
                                   String mobilePin,
                                   String mobileKeySetId,
                                   String transportKey,
                                   String macKey,
                                   String dataEncryptionKey,
                                   String rnsRegistrationId) {
        this.id = id;
        this.paymentAppId = paymentAppId;
        this.paymentAppInstId = paymentAppInstId;
        this.deviceFingerprint = deviceFingerprint;
        this.mobilePin = mobilePin;
        this.mobileKeySetId = mobileKeySetId;
        this.transportKey = transportKey;
        this.macKey = macKey;
        this.dataEncryptionKey = dataEncryptionKey;
        this.rnsRegistrationId = rnsRegistrationId;
    }

    public ApplicationInstanceInfo() {
        this(null, null, null, null, null, null, null, null, null, null );
    }

}
