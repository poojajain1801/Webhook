package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * TokennInfo Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class DynParams {

    private String api;
    private String sc;

   public DynParams(String api,String sc) {
       this.api=api;
       this.sc=sc;
    }

    public DynParams() {
    }
}