package com.comviva.mfs.hce.appserver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
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

    public ConfigurationManagement() {
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getHvtLimit() {
        return hvtLimit;
    }

    public String getIsHvtSupported() {
        return isHvtSupported;
    }

    public Timestamp getCreatedOn() {
        return (createdOn);
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHvtLimit(String hvtLimit) {
        this.hvtLimit = hvtLimit;
    }

    public void setIsHvtSupported(String isHvtSupported) {
        this.isHvtSupported = isHvtSupported;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }
}
