package com.comviva.mfs.promotion.modules.card_management.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Entity
@Getter
@Table(name = "CARD_DETAILS")
@ToString
@EqualsAndHashCode
public class CardDetails {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;


    @Column(name = "USER_ID")
    private final String userID;

    @Column(name = "TOKEN_REFERENCE")
    private final String tokenReference;

    @Column(name = "PAN_REFERENCE")
    private final String panReference;

    @Column(name = "TOKEN_INFO")
    private final String tokenInfo;

    @Column(name = "TOKEN_STATUS")
    private final String tokenStatus;

    public CardDetails(String id, String userID, String tokenReference, String panReference, String tokenInfo, String tokenStatus) {
        this.id = id;
        this.userID = userID;
        this.tokenReference = tokenReference;
        this.panReference = panReference;
        this.tokenInfo = tokenInfo;
        this.tokenStatus = tokenStatus;
    }

    CardDetails(){this(null,null,null,null,null,null);}
}
