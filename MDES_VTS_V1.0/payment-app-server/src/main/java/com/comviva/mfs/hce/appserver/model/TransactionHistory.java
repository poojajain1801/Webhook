package com.comviva.mfs.hce.appserver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 5/23/2017.
 */
@Entity
@Getter
@Setter
@Table(name = "TRANSACTION_HISTORY")
@ToString
@EqualsAndHashCode
public class TransactionHistory {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "TOKEN_UNIQUE_REFERENCE")
    private String tokenUniqueReference;

    @Column(name = "TRANSACTION_DETAILS")
    private String transactionDetails;

    public TransactionHistory(String id, String tokenUniqueReference, String transactionDetails) {
        this.id = id;
        this.tokenUniqueReference = tokenUniqueReference;
        this.transactionDetails = transactionDetails;
    }
    public TransactionHistory()
    {

    }

}
