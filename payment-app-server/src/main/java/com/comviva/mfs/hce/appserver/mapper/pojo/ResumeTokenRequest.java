package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ResumeTokenRequest.
 * Created by Amgoth.madan on 5/10/2017.
 */
@Getter
@Setter
public class ResumeTokenRequest {

    private String userId;
    private String activationCode;
    private String vProvisionedTokenID;
    private String reasonCode;
    private String reasonDesc;


    public ResumeTokenRequest(String userId, String activationCode, String vProvisionedTokenID, String reasonCode, String reasonDesc) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.vProvisionedTokenID=vProvisionedTokenID;
        this.reasonCode=reasonCode;
        this.reasonDesc=reasonDesc;
    }

    public ResumeTokenRequest() {
    }
}