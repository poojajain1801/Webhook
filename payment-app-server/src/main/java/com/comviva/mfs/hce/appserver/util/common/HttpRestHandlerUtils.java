package com.comviva.mfs.hce.appserver.util.common;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * Created by tanmay.patel on 1/31/2017.
 */
public interface HttpRestHandlerUtils {

    String restfulServieceConsumer(String url, MultiValueMap parametersMap);
    public ResponseEntity<String> httpPost(String url, MultiValueMap parametersMap);

}
