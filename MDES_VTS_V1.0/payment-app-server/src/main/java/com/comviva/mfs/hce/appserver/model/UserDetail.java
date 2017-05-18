package com.comviva.mfs.hce.appserver.model;

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

    @Column(name = "client_wallet_accountid")
    private /*final*/ String clientWalletAccountid;

    public UserDetail(String id, String userName, String activationCode, String userstatus,String clientWalletAccountid) {
        this.id = id;
        this.userName = userName;
        this.activationCode = activationCode;
        this.userstatus = userstatus;
        this.clientWalletAccountid=clientWalletAccountid;
    }

    public UserDetail() {
        this(null, null,null, null,null);
    }


}
