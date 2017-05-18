package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * EnrollPan Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class BillingAddress {

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    public BillingAddress(String line1, String line2, String city, String state,String country,String postalCode) {
        this.line1=line1;
        this.line2=line2;
        this.city=city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }
    public BillingAddress() {
    }
}