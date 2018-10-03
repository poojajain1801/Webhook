package com.comviva.hceservice.responseobject;

import java.io.Serializable;

public class StepUpRequest implements Serializable {

    public StepUpRequest(String identifier, String method, String value) {

        this.identifier = identifier;
        this.method = method;
        this.value = value;
    }


    private String identifier;
    private String method;
    private String value;


    public String getIdentifier() {

        return identifier;
    }


    public String getMethod() {

        return method;
    }


    public String getValue() {

        return value;
    }
}
