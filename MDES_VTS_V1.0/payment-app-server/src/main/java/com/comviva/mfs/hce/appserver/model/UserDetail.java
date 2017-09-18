package com.comviva.mfs.hce.appserver.model;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the USER_DETAILS database table.
 *
 */
@Entity
@Table(name="USER_DETAILS")
public class UserDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name="ACTIVATION_CODE")
    private String activationCode;

    @Column(name="CLIENT_DEVICE_ID")
    private String clientDeviceId;

    @Column(name="CLIENT_WALLET_ACCOUNT_ID")
    private String clientWalletAccountId;

    @Column(name="PAYMENT_APP_INSTANCE_ID")
    private String paymentAppInstanceId;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="USER_STATUS")
    private String userStatus;

    public UserDetail() {
    }

    public UserDetail(String id, String userName, String activationCode, String userstatus,String clientWalletAccountid, String clientDeviceId, String paymentAppInstId) {
        this.id = id;
        this.userName = userName;
        this.activationCode = activationCode;
        this.userStatus = userstatus;
        this.clientWalletAccountId=clientWalletAccountid;
        this.clientDeviceId=clientDeviceId;
        this.paymentAppInstanceId = paymentAppInstId;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivationCode() {
        return this.activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getClientDeviceId() {
        return this.clientDeviceId;
    }

    public void setClientDeviceId(String clientDeviceId) {
        this.clientDeviceId = clientDeviceId;
    }

    public String getClientWalletAccountId() {
        return this.clientWalletAccountId;
    }

    public void setClientWalletAccountId(String clientWalletAccountId) {
        this.clientWalletAccountId = clientWalletAccountId;
    }

    public String getPaymentAppInstanceId() {
        return this.paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return this.userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

}