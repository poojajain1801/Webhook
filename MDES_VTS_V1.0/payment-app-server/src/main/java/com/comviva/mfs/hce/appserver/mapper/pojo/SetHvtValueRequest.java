package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@Getter
@Setter
public class SetHvtValueRequest {

    private String userName ;
    private String requestId ;
    private String hvtLimit ;
    private String isHvtSupported ;

    public SetHvtValueRequest(String userName, String requestId, String hvtLimit, String isHvtSupported) {
        this.userName = userName;
        this.requestId = requestId;
        this.hvtLimit = hvtLimit;
        this.isHvtSupported = isHvtSupported;
    }

    public SetHvtValueRequest() {
    }
}
