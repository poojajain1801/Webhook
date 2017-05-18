package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * StepUpRequest
 * Created by amgoth.naik on 25/04/2017.
 */
@Getter
@Setter
public class StepUpRequest {
    private String method;
    private String value;
    private String source;
    private String identifier;
    private String requestPayload;

    public StepUpRequest(String method,String value,String source,String identifier,String requestPayload) {
        this.method=method;
        this.value=value;
        this.source=source;
        this.identifier=identifier;
        this.requestPayload=requestPayload;
    }

    public StepUpRequest() {
    }
}
