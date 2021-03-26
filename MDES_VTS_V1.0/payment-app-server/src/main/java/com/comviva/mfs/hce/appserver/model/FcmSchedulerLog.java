package com.comviva.mfs.hce.appserver.model;


import java.sql.Timestamp;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="FCM_SCHEDULER_LOG")
public class FcmSchedulerLog {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name="REQUEST_ID")
    private String requestId;

    @Column(name="RNS_REGISTRATION_ID")
    private String rnsRegistrationId;

    @Column(name="STATUS")
    private String status;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;
}
