package com.comviva.mfs.hce.appserver.model;


import java.sql.Timestamp;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="FCM_SCHEDULER_LOG")
public class FcmSchedulerLog {
    @Id
    @Column(name="RNS_REGISTRATION_ID")
    private String rnsRegistrationId;

    @Column(name="STATUS")
    private String status;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;
}
