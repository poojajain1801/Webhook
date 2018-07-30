package com.comviva.hceservice.constants;
public enum Tags {

    //Shared Preferenes Names
    EXCEPTION_LOG("EXCEPTION");

    private String tag;

    Tags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

}