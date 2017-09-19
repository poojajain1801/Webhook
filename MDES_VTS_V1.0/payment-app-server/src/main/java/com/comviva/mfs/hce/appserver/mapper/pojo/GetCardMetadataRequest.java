package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * GetCardMetadataRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class GetCardMetadataRequest {

    private String vpanEnrollmentID;

    public GetCardMetadataRequest(String vpanEnrollmentID) {
        this.vpanEnrollmentID = vpanEnrollmentID;
    }

    public GetCardMetadataRequest() {
    }
}