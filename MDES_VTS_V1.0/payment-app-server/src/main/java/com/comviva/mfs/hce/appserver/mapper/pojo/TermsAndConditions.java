package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * TermsAndConditions
 * Created by amgoth madan on 4/25/2017.
 */
@Getter
@Setter
public class TermsAndConditions {

    private String id;
    private Date date;
    public TermsAndConditions(String id, Date date) {
        this.id=id;
        this.date =(date);
    }
    public TermsAndConditions() {
    }
}