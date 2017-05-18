package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ExpirationDate pojo
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class ExpirationDate {

    private String month;
    private String year;

    public ExpirationDate(String month, String year) {
        this.month=month;
        this.year=year;
    }

    public ExpirationDate() {
    }
}