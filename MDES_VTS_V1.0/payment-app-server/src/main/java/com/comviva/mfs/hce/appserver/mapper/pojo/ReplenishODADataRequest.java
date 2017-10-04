package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ReplenishODADataRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class ReplenishODADataRequest {

   private String vprovisionedTokenID;

    public ReplenishODADataRequest(String vprovisionedTokenID) {
        this.vprovisionedTokenID = vprovisionedTokenID;
    }

    public ReplenishODADataRequest() {
    }
}