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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;


/**
 * The persistent class for the CARD_DETAILS_VISA database table.
 *
 */
@Entity
@Table(name="VISA_CARD_DETAILS")
public class VisaCardDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="CARD_ID")
    private String cardId;

    @Column(name="CARD_NUMBER_SUFFIX")
    private String cardNumberSuffix;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;

    private String status;

    @Column(name="V_PAN_ENROLLMENT_ID")
    private String vPanEnrollmentId;

    @Column(name="V_PROVISIONED_TOKEN_ID")
    private String vProvisionedTokenId;


    @Column(name="CARD_IDENTIFIER")
    private String cardIdentifier;



    @Column(name="REPLENISH_ON")
    private Timestamp replenishOn;


    //bi-directional many-to-one association to DeviceInfo
    @ManyToOne
    @JoinColumn(name="CLIENT_DEVICE_ID")
    private DeviceInfo deviceInfo;

    public VisaCardDetails() {
    }


    public Timestamp getReplenishOn() {
        return (replenishOn);
    }

    public void setReplenishOn(Timestamp replenishOn) {
        this.replenishOn = (replenishOn);
    }
    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNumberSuffix() {
        return this.cardNumberSuffix;
    }

    public void setCardNumberSuffix(String cardNumberSuffix) {
        this.cardNumberSuffix = cardNumberSuffix;
    }

    public Timestamp getCreatedOn() {
        return (this.createdOn);
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }

    public Timestamp getModifiedOn() {
        return (this.modifiedOn);
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

    public String getVPanEnrollmentId() {
        return this.vPanEnrollmentId;
    }

    public void setVPanEnrollmentId(String vPanEnrollmentId) {
        this.vPanEnrollmentId = vPanEnrollmentId;
    }

    public String getVProvisionedTokenId() {
        return this.vProvisionedTokenId;
    }

    public void setVProvisionedTokenId(String vProvisionedTokenId) {
        this.vProvisionedTokenId = vProvisionedTokenId;
    }

    public DeviceInfo getDeviceInfo() {
        return this.deviceInfo;
    }


    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getCardIdentifier() {
        return cardIdentifier;
    }

    public void setCardIdentifier(String cardIdentifier) {
        this.cardIdentifier = cardIdentifier;
    }

}
