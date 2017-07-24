package com.comviva.mfs.hce.appserver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 5/19/2017.
 */
@Entity
@Getter
@Setter
@Table(name = "TRANSACTION_REG_DETAILS")
@ToString
@EqualsAndHashCode
public class TransactionRegDetails {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstanceId;

    @Column(name = "TOKEN_UNIQUE_REFERENCE")
    private String tokenUniqueReference;

    @Column(name = "REG_CODE_1")
    private String regCode1;

    @Column(name = "REG_CODE_2")
    private String regCode2;

    @Column(name = "AUTH_CODE")
    private String authCode;

    @Column(name = "TDS_URL")
    private String tdsUrl;

    public TransactionRegDetails(String id, String tokenUniqueReference, String regCode1,
                                 String regCode2, String authCode, String tdsUrl) {
        this.id = id;
        this.tokenUniqueReference = tokenUniqueReference;
        this.regCode1 = regCode1;
        this.regCode2 = regCode2;
        this.authCode = authCode;
        this.tdsUrl = tdsUrl;
    }

    public TransactionRegDetails() {
    }
}

