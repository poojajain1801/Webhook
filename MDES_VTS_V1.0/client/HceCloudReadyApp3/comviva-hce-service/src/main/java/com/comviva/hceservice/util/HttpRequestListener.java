package com.comviva.hceservice.util;


import com.comviva.hceservice.common.RestResponse;

public abstract class HttpRequestListener {
    private HttpResponse response;

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public abstract void onPreExecute();
    public abstract void onTaskComplete();


}
