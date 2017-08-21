package com.comviva.mfs.hce.appserver.model;

import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TRANSACTION_REG_DETAILS database table.
 *
 */
@Entity
@Table(name="TRANSACTION_REG_DETAILS")
public class TransactionRegDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name="AUTH_CODE")
    private String authCode;

    @Column(name="PAYMENT_APP_INSTANCE_ID")
    private String paymentAppInstanceId;

    @Column(name="REG_CODE_1")
    private String regCode1;

    @Column(name="REG_CODE_2")
    private String regCode2;

    @Column(name="TDS_URL")
    private String tdsUrl;

    @Column(name="TOKEN_UNIQUE_REFERENCE")
    private String tokenUniqueReference;

    public TransactionRegDetails() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthCode() {
        return this.authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getPaymentAppInstanceId() {
        return this.paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getRegCode1() {
        return this.regCode1;
    }

    public void setRegCode1(String regCode1) {
        this.regCode1 = regCode1;
    }

    public String getRegCode2() {
        return this.regCode2;
    }

    public void setRegCode2(String regCode2) {
        this.regCode2 = regCode2;
    }

    public String getTdsUrl() {
        return this.tdsUrl;
    }

    public void setTdsUrl(String tdsUrl) {
        this.tdsUrl = tdsUrl;
    }

    public String getTokenUniqueReference() {
        return this.tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

}