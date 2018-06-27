package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by amgoth.madan on 5/10/2017.
 */
@Getter
public class GetStepUpOptionsRequest {

    private String vProvisionedTokenID;

    public GetStepUpOptionsRequest() {
    }

    public GetStepUpOptionsRequest(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }
}
