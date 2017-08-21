package com.comviva.mfs.hce.appserver.model;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.io.Serializable;



/**
 * The persistent class for the CARD_DETAILS_VISA database table.
 *
 */
@Entity
@Table(name="CARD_DETAILS_VISA")
public class VisaCardDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String cardnumbersuffix;

    private String status;

    @Column(name="USER_NAME")
    private String userName;

    private String vpanenrollmentid;

    public VisaCardDetails() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardnumbersuffix() {
        return this.cardnumbersuffix;
    }

    public void setCardnumbersuffix(String cardnumbersuffix) {
        this.cardnumbersuffix = cardnumbersuffix;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVpanenrollmentid() {
        return this.vpanenrollmentid;
    }

    public void setVpanenrollmentid(String vpanenrollmentid) {
        this.vpanenrollmentid = vpanenrollmentid;
    }

}