package com.comviva.mfs.hce.appserver.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;



/**
 * The persistent class for the SERVICE_DATA database table.
 *
 */
@Entity
@Table(name="SERVICE_DATA")
public class ServiceData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Lob
    private byte[] request;

    @Lob
    private byte[] response;

    @Column(name="SERVICE_ID")
    private String serviceId;

    public ServiceData() {
    }

    public ServiceData(String id, String serviceId, byte[] request, byte[] response) {
        this.id = id;
        this.serviceId = serviceId;
        this.request = request;
        this.response = response;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getRequest() {
        return this.request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public byte[] getResponse() {
        return this.response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

}