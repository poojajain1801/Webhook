package com.comviva.mfs.hce.appserver.model;

import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import javax.persistence.*;
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

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="SERVICE_TYPE")
	private String serviceType;

	@Lob
	private byte[] request;

	@Lob
	private byte[] response;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	public AuditTrail() {
	}

	public String getRequestId() {
		return this.requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = (createdOn);
	}

	public byte[] getRequest() {
		return this.request;
	}

	public void setRequest(byte[] request) {
		this.request = (request);
	}

	public byte[] getResponse() {
		return this.response;
	}

	public void setResponse(byte[] response) {
		this.response = (response);
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getClientDeviceId() {
		return clientDeviceId;
	}

	public void setClientDeviceId(String clientDeviceId) {
		this.clientDeviceId = clientDeviceId;
	}
}