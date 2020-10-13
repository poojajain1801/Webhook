package com.comviva.mfs.hce.appserver.model;

import org.hibernate.annotations.GenericGenerator;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


/**
 * The persistent class for the AUDIT_TRAIL database table.
 * 
 */
@Entity
@Table(name="AUDIT_TRAIL")
public class AuditTrail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name="REQUEST_ID")
	private String requestId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CLIENT_DEVICE_ID")
	private String clientDeviceId;

/*	@Column(name="TOTAL_TIME_TAKEN")
	private String totalTimeTaken;*/

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="SERVICE_TYPE")
	private String serviceType;

	private byte[] request;

	private byte[] response;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	public AuditTrail() {
	}

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setClientDeviceId(String clientDeviceId) {
        this.clientDeviceId = clientDeviceId;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getClientDeviceId() {
        return clientDeviceId;
    }


    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public byte[] getRequest() {
        return request;
    }

    public byte[] getResponse() {
        return response;
    }
}