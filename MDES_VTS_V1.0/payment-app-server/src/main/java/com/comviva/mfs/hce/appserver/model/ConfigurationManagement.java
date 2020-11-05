package com.comviva.mfs.hce.appserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="CONFIGURATION_MANAGEMENT")
public class ConfigurationManagement {

    private static final long serialVersionUID = 1L;

    @Id
    private String requestId ;

    private String userId;

    private String hvtLimit ;

    private String isHvtSupported ;

    private Timestamp createdOn;

}
