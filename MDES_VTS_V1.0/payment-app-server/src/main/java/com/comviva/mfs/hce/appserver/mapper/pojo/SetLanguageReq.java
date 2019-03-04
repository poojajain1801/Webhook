package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tanmay.patel on 10/4/2018.
 */
@Getter
@Setter
public class SetLanguageReq {

    private String userId ;
    private String languageCode ;

    public SetLanguageReq(String userId, String languageCode) {
        this.userId = userId;
        this.languageCode = languageCode;
    }

    public SetLanguageReq() {

    }

}
