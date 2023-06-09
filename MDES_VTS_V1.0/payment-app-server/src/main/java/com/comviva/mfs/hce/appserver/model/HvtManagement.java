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

    @EmbeddedId
    private HvtManagementPK id;

    @Column(name = "IS_HVT_SUPPORTED")
    private String isHvtSupported;

    @Column(name = "HVT_LIMIT")
    private String hvtLimit;

    @Column(name="COLOR_VALUE")
    private String colorValue;

    @Column(name="TRANSACTION_TIME")
    private String transactionTime;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;
}
