package com.comviva.mfs.hce.appserver.model;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * The persistent class for the USER_DETAILS database table.
 *
 */
@Entity
@Table(name="USER_DETAILS")
public class UserDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="CLIENT_WALLET_ACCOUNT_ID")
    private String clientWalletAccountId;

    @Column(name="ACTIVATION_CODE")
    private String activationCode;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;

    private String status;

    @Column(name="USER_ID")
    private String userId;

    //bi-directional many-to-one association to DeviceInfo
    @OneToMany(mappedBy="userDetail",cascade = CascadeType.ALL)
    private List<DeviceInfo> deviceInfos;

    public UserDetail() {
    }




    public String getClientWalletAccountId() {
        return this.clientWalletAccountId;
    }

    public void setClientWalletAccountId(String clientWalletAccountId) {
        this.clientWalletAccountId = clientWalletAccountId;
    }

    public String getActivationCode() {
        return this.activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }

    public Timestamp getModifiedOn() {
        return this.modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = (modifiedOn);
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<DeviceInfo> getDeviceInfos() {
        return this.deviceInfos;
    }

    public void setDeviceInfos(List<DeviceInfo> deviceInfos) {
        this.deviceInfos = (deviceInfos);
    }

    public DeviceInfo addDeviceInfo(DeviceInfo deviceInfo) {
        getDeviceInfos().add(deviceInfo);
        deviceInfo.setUserDetail(this);

        return deviceInfo;
    }

    public DeviceInfo removeDeviceInfo(DeviceInfo deviceInfo) {
        getDeviceInfos().remove(deviceInfo);
        deviceInfo.setUserDetail(null);

        return deviceInfo;
    }
}