package com.comviva.mfs.hce.appserver.model;

import java.io.Serializable;
import javax.persistence.*;
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