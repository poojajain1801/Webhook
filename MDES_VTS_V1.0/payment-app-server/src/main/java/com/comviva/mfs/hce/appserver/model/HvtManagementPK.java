package com.comviva.mfs.hce.appserver.model;

import java.io.Serializable;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;


@Data
@Embeddable
public class HvtManagementPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name="REQUEST_ID")
    private String requestId;

    @Column(name="PAYMENT_APP_ID")
    private String paymentAppId;
}
