package com.comviva.hceservice.util;

/**
 * Response received from server.
 * Created by tarkeshwar.v on 3/3/2017.
 */

public class HttpResponse {
    public static final String OK = "OK";

    /** Http Status code */
    private int statusCode;
    /** Response received from server */
    private String response;
    /** Status of the request */
    private String reqStatus;

    public HttpResponse() {
        reqStatus = HttpResponse.OK;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String error) {
        this.reqStatus = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
