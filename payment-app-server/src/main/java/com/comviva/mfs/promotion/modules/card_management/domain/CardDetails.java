package com.comviva.mfs.promotion.modules.card_management.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Entity
@Getter
@Setter
@Table(name = "CARD_DETAILS")
@ToString
@EqualsAndHashCode
public class CardDetails {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstanceId;

    @Column(name = "TOKEN_UNIQUE_REFERENCE")
    private String tokenUniqueReference;

    @Column(name = "PAN_UNIQUE_REFERENCE")
    private String panUniqueReference;

    @Column(name = "TOKEN_INFO")
    private String tokenInfo;

    @Column(name = "TOKEN_STATUS")
    private String tokenStatus;

    public CardDetails(String id, String userName, String paymentAppInstanceId, String tokenUniqueReference,
                       String panUniqueReference, String tokenInfo, String tokenStatus) {
        this.id = id;
        this.userName = userName;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.panUniqueReference = panUniqueReference;
        this.tokenInfo = tokenInfo;
        this.tokenStatus = tokenStatus;
    }

    public CardDetails(){this(null,null,null,null,null,null, null);}
}
