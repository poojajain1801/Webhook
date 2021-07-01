package com.comviva.mfs.hce.appserver.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the CARD_DETAILS database table.
 *
 */
@Entity
@Table(name="CARD_DETAILS")
public class CardDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="CARD_ID")
    private String cardId;

    @Column(name="CARD_IDENTIFIER")
    private String cardIdentifier;

    @Column(name="CARD_SUFFIX")
    private String cardSuffix;

    @Column(name="CARD_TYPE")
    private String cardType;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MASTER_PAYMENT_APP_INSTANCE_ID")
    private String masterPaymentAppInstanceId;

    @Column(name="MASTER_TOKEN_INFO")
    private String masterTokenInfo;

    @Column(name="MASTER_TOKEN_UNIQUE_REFERENCE")
    private String masterTokenUniqueReference;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column(name="PAN_UNIQUE_REFERENCE")
    private String panUniqueReference;

    @Column(name="REPLENISH_ON")
    private Timestamp replenishOn;

    private String status;

    @Column(name="TOKEN_SUFFIX")
    private String tokenSuffix;

    @Column(name="VISA_PROVISION_TOKEN_ID")
    private String visaProvisionTokenId;

    //bi-directional many-to-one association to DeviceInfo
    @ManyToOne
    @JoinColumn(name="CLIENT_DEVICE_ID")
    private DeviceInfo deviceInfo;

    @Column(name="REPERSO_STATUS")
    private String repersoStatus;

    public CardDetails() {
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardIdentifier() {
        return this.cardIdentifier;
    }

    public void setCardIdentifier(String cardIdentifier) {
        this.cardIdentifier = cardIdentifier;
    }

    public String getCardSuffix() {
        return this.cardSuffix;
    }

    public void setCardSuffix(String cardSuffix) {
        this.cardSuffix = cardSuffix;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Timestamp getCreatedOn() {
        return (this.createdOn);
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }

    public String getMasterPaymentAppInstanceId() {
        return this.masterPaymentAppInstanceId;
    }

    public void setMasterPaymentAppInstanceId(String masterPaymentAppInstanceId) {
        this.masterPaymentAppInstanceId = masterPaymentAppInstanceId;
    }

    public String getMasterTokenInfo() {
        return this.masterTokenInfo;
    }

    public void setMasterTokenInfo(String masterTokenInfo) {
        this.masterTokenInfo = masterTokenInfo;
    }

    public String getMasterTokenUniqueReference() {
        return this.masterTokenUniqueReference;
    }

    public void setMasterTokenUniqueReference(String masterTokenUniqueReference) {
        this.masterTokenUniqueReference = masterTokenUniqueReference;
    }

    public Timestamp getModifiedOn() {
        return (this.modifiedOn);
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = (modifiedOn);
    }

    public String getPanUniqueReference() {
        return this.panUniqueReference;
    }

    public void setPanUniqueReference(String panUniqueReference) {
        this.panUniqueReference = panUniqueReference;
    }

    public Timestamp getReplenishOn() {
        return (this.replenishOn);
    }

    public void setReplenishOn(Timestamp replenishOn) {
        this.replenishOn = (replenishOn);
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTokenSuffix() {
        return this.tokenSuffix;
    }

    public void setTokenSuffix(String tokenSuffix) {
        this.tokenSuffix = tokenSuffix;
    }

    public String getVisaProvisionTokenId() {
        return this.visaProvisionTokenId;
    }

    public void setVisaProvisionTokenId(String visaProvisionTokenId) {
        this.visaProvisionTokenId = visaProvisionTokenId;
    }

    public DeviceInfo getDeviceInfo() {
        return this.deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}