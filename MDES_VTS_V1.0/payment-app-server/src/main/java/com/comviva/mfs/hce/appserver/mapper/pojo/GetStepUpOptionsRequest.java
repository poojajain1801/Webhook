package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by amgoth.madan on 5/10/2017.
 */

public class GetStepUpOptionsRequest{

    private String vProvisionedTokenID;

    public GetStepUpOptionsRequest(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }
    public GetStepUpOptionsRequest() {

    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }
}
