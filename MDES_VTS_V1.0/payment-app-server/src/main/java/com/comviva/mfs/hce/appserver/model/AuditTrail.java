/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
