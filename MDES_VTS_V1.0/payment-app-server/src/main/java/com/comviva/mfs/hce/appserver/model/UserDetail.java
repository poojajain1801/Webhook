package com.comviva.mfs.hce.appserver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Entity
@Getter
@Setter
@Table(name = "USER_DETAILS")
@ToString
@EqualsAndHashCode
public class UserDetail {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

    @Column(name = "USER_NAME")
    private final String userName;

    @Column(name = "activation_code")
    private final String activationCode;

    @Column(name = "user_status")
    private String userstatus;

    @Column(name = "client_wallet_account_id")
    private String clientWalletAccountid;

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstId;

    @Column(name = "client_device_id")
    private String clientDeviceId;

    public UserDetail(String id, String userName, String activationCode, String userstatus,String clientWalletAccountid, String clientDeviceId, String paymentAppInstId) {
        this.id = id;
        this.userName = userName;
        this.activationCode = activationCode;
        this.userstatus = userstatus;
        this.clientWalletAccountid=clientWalletAccountid;
        this.clientDeviceId=clientDeviceId;
        this.paymentAppInstId = paymentAppInstId;
    }

    public UserDetail() {
        this(null, null,null, null, null,null,null);
    }
}