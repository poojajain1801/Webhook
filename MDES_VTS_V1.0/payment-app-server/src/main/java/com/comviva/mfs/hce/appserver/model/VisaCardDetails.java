package com.comviva.mfs.hce.appserver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Amgoth.madan on 5/15/2017.
 */
@Entity
@Getter
@Setter
@Table(name = "CARD_DETAILS_VISA")
@ToString
@EqualsAndHashCode
public class VisaCardDetails {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "CRAD_NUMBER_SUFIX")
    private String cardnumbersuffix;

    @Column(name = "V_PAN_ENROLLMENT_ID")
    private String vpanenrollmentid;

    @Column(name = "STATUS")
    private String status;

    public VisaCardDetails(String id, String userName,String cardnumberSuffix,String vpanenrollmentid,String status) {
        this.id = id;
        this.userName = userName;
        this.cardnumbersuffix=cardnumberSuffix;
        this.vpanenrollmentid=vpanenrollmentid;
        this.status=status;
    }

    public VisaCardDetails(){this(null,null,null,null,null);}
}