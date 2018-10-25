package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by amgoth.madan on 5/10/2017.
 */
@Getter
@Setter
public class GetTokenStatusRequest {

    private String vprovisionedTokenID;

    public GetTokenStatusRequest(String vprovisionedTokenID) {
        this.vprovisionedTokenID = vprovisionedTokenID;
    }

    public GetTokenStatusRequest() {

    }
}
