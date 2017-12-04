package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by amgoth.madan on 5/10/2017.
 */
@Getter
public class GetContentRequest{

    private String guid;

    public GetContentRequest(String serviceId, String guid) {
        this.guid = guid;
    }

    public GetContentRequest() {
    }
}
