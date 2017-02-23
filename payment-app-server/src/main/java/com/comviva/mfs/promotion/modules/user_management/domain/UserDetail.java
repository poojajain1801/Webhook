package com.comviva.mfs.promotion.modules.user_management.domain;

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
@Table(name = "USER_DETAILS")
@ToString
@EqualsAndHashCode
public class UserDetail {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

    @Column(name = "USER_NAME")
    private final String userName;

    @Column(name = "activation_code")
    private final String activationCode;

    @Column(name = "user_status")
    private /*final*/ String userstatus;



    public UserDetail(String id, String userName, String activationCode, String userstatus) {
        this.id = id;
        this.userName = userName;
        this.activationCode = activationCode;
        this.userstatus = userstatus;
    }

    public UserDetail() {
        this(null, null,null, null);
    }


}
