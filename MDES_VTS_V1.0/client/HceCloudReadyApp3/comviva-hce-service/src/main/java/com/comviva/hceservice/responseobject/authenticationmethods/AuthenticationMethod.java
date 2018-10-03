package com.comviva.hceservice.responseobject.authenticationmethods;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Additional Authentication if required while digitization process.
 */
public class AuthenticationMethod {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private String value;


    public String getId() {

        return id;
    }


    public void setId(String id) {

        this.id = id;
    }


    public String getType() {

        return type;
    }


    public void setType(String type) {

        this.type = type;
    }


    public String getValue() {

        return value;
    }


    public void setValue(String value) {

        this.value = value;
    }
}
