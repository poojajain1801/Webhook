package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * TokennInfo Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class TokennInfo {

    private HceData hceData;


   public TokennInfo(HceData hceData) {
       this.hceData=hceData;
    }

    public TokennInfo() {
    }
}