package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tanmay.patel on 10/4/2018.
 */
@Getter
@Setter
public class GetLanguageReq {

    private String userId ;

    public GetLanguageReq(String userId) {
        this.userId = userId;
    }

    public GetLanguageReq() {

    }


}
