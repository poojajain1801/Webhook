package com.comviva.mfs.hce.appserver.util.common;

import org.springframework.util.MultiValueMap;

/**
 * Created by Tanmay.Patel on 1/17/2017.
 */
public interface HttpRestHandeler {
    String restfulServieceConsumer(String url, MultiValueMap parametersMap);
}
