package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;

@Getter
@Setter
public class UnRegisterReq {

    private String imei;
    private String userId;

    public UnRegisterReq(String imei, String userId) {
        this.imei = imei;
        this.userId = userId;
    }

    public UnRegisterReq(){

    }
}
