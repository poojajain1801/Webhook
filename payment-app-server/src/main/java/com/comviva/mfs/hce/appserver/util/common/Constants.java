package com.comviva.mfs.hce.appserver.util.common;

/**
 * Created by sumit.das on 12/21/2016.
 */
public enum  Constants {
    DEFAULT_ENCODING("UTF-8"), DEFAULT_LOCALE("en");

    private final String constantValue;

    Constants(String constantValue) {
        this.constantValue = constantValue;
    }

    public String getValue(){
        return constantValue;
    }
}
