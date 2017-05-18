package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * HceData Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class HceData {

    private DynParams dynParams;


   public HceData(DynParams dynParams) {
        this.dynParams=dynParams;
    }

    public HceData() {
    }
}