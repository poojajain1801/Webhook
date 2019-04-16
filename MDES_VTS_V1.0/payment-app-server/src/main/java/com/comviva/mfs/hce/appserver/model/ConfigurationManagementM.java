package com.comviva.mfs.hce.appserver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@Entity
@Table(name="CONFIGURATION_MANAGEMENT_M")
public class ConfigurationManagementM implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String requestId ;

    private String userId;

    private String hvtLimit ;

    private String isHvtSupported ;

    private Timestamp createdOn;

    private Timestamp modifiedOn;

    private String status ;

    public ConfigurationManagementM() {
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

    public Timestamp getModifiedOn() {
        return (modifiedOn);
    }

    public String getStatus() {
        return status;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = (modifiedOn);
    }
}
