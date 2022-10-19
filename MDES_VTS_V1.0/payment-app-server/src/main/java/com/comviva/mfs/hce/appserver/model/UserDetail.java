/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @Column(name="LANGUAGE_CODE")
    private String languageCode;

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

    public Timestamp getCreatedOn() {
        return (this.createdOn);
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }

    public String getActivationCode() {
        return this.activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<DeviceInfo> getDeviceInfos() {
        return (this.deviceInfos);
    }

    public Timestamp getModifiedOn() {
        return (this.modifiedOn);
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = (modifiedOn);
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

