package com.comviva.mfs.promotion.modules.common.sessionmanagement.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "SESSION_INFO")
@ToString
@EqualsAndHashCode
public class SessionInfo {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "session_code")
    private String sessionCode;

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstanceId;

    @Column(name = "authentication_code")
    private String authenticationCode;

    @Column(name = "expiry_time_stamp")
    private String expiryTimeStamp;

    @Column(name = "valid_for_seconds")
    private int validForSeconds;

    @Column(name = "token_unique_reference")
    private String tokenUniqueReference;

    @Column(name = "mobile_session_key_conf")
    private String mobileSessionKeyConf;

    @Column(name = "mobile_session_key_mac")
    private String mobileSessionKeyMac;

    @Column(name = "m2c_counter")
    private int m2cCounter;

    @Column(name = "c2m_counter")
    private int c2mCounter;

    @Column(name = "session_first_use_time")
    private String sessionFirstUse;

    public SessionInfo(String id,
                       String sessionCode,
                       String paymentAppInstanceId,
                       String authenticationCode,
                       String expiryTimeStamp,
                       int validForSeconds,
                       String tokenUniqueReference,
                       String mobileSessionKeyConf,
                       String mobileSessionKeyMac,
                       int m2cCounter,
                       int c2mCounter,
                       String sessionFirstUse) {
        this.id = id;
        this.sessionCode = sessionCode;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.authenticationCode = authenticationCode;
        this.expiryTimeStamp = expiryTimeStamp;
        this.validForSeconds = validForSeconds;
        this.tokenUniqueReference = tokenUniqueReference;
        this.mobileSessionKeyConf = mobileSessionKeyConf;
        this.mobileSessionKeyMac = mobileSessionKeyMac;
        this.m2cCounter =  m2cCounter;
        this.c2mCounter = c2mCounter;
        this.sessionFirstUse = sessionFirstUse;
    }

    public SessionInfo() {
        this(null, null, null, null, null, 0, null, null, null, 0, 0, null);
    }
}
