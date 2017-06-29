package com.comviva.mfs.hce.appserver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Tanmay.Patel on 2/2/2017.
 */
@Entity
@Setter
@Getter
@Table(name = "SERVICE_DATA")
@ToString
@EqualsAndHashCode
public class ServiceData {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

/*
    @Column(name = "user_name")
    private String userName;
*/

    @Column(name = "service_id")
    private  String serviceId;

    @Column(name = "request")
    private  String request;

    @Column(name = "response")
    private  String response;

    public ServiceData(String id, String serviceId, String request, String response) {
        this.id = id;
        this.serviceId = serviceId;
        this.request = request;
        this.response = response;
    }

    public ServiceData() {this(null,null,null,null);
    }


}
