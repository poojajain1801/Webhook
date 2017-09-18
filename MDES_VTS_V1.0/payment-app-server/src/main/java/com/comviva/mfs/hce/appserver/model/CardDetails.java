package com.comviva.mfs.hce.appserver.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;



/**
 * The persistent class for the CARD_DETAILS database table.
 *
 */
@Entity
@Table(name="CARD_DETAILS")
public class CardDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name="PAN_UNIQUE_REFERENCE")
    private String panUniqueReference;

    @Column(name="PAYMENT_APP_INSTANCE_ID")
    private String paymentAppInstanceId;

    @Column(name="TOKEN_INFO")
    private String tokenInfo;

    @Column(name="TOKEN_STATUS")
    private String tokenStatus;

    @Column(name="TOKEN_UNIQUE_REFERENCE")
    private String tokenUniqueReference;

    public CardDetails() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPanUniqueReference() {
        return this.panUniqueReference;
    }

    public void setPanUniqueReference(String panUniqueReference) {
        this.panUniqueReference = panUniqueReference;
    }

    public String getPaymentAppInstanceId() {
        return this.paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getTokenInfo() {
        return this.tokenInfo;
    }

    public void setTokenInfo(String tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getTokenStatus() {
        return this.tokenStatus;
    }

    public void setTokenStatus(String tokenStatus) {
        this.tokenStatus = tokenStatus;
    }

    public String getTokenUniqueReference() {
        return this.tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

}