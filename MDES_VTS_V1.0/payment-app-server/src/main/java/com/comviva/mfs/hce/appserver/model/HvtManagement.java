package com.comviva.mfs.hce.appserver.model;


import java.sql.Timestamp;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class used for HVT Management
 *
 *
 */


@Data
@Entity
@Table(name="HVT_MANAGEMENT")
public class HvtManagement {

//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name="REQUEST_ID")
//    private String requestId;
//
//    @Column(name="PAYMENT_APP_ID")
//    private String paymentAppId;

    @EmbeddedId
    private HvtManagementPK id;

    @Column(name = "IS_HVT_SUPPORTED")
    private String isHvtSupported;

    @Column(name = "HVT_LIMIT")
    private String hvtLimit;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;
}
