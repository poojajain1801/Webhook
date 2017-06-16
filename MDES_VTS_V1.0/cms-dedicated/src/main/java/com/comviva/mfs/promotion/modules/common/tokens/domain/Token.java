package com.comviva.mfs.promotion.modules.common.tokens.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "TOKEN_INFO")
@ToString
@EqualsAndHashCode
public class Token {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

    @Column(name = "payment_app_instance_id")
    private final String paymentAppInstId;

    @Column(name = "token_unique_reference")
    private final String tokenUniqueReference;

    @Column(name = "token_type")
    private final String tokenType;

    @Column(name = "card_profile")
    private final String cardProfile;

    @Column(name = "icc_kek")
    private final String iccKek;

    @Column(name = "kek_id")
    private final String kekId;

    @Column(name = "state")
    private String state;

    public Token(String id, String paymentAppInstId, String tokenUniqueReference, String tokenType, String cardProfile, String iccKek, String kek_id, String state) {
        this.id = id;
        this.paymentAppInstId = paymentAppInstId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.tokenType = tokenType;
        this.cardProfile = cardProfile;
        this.iccKek = iccKek;
        this.kekId = kek_id;
        this.state = state;
    }

    public Token() {
        this(null, null, null, null, null, null, null, null);
    }
}
