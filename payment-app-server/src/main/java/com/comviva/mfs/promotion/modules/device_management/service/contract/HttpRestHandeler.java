package com.comviva.mfs.promotion.modules.device_management.service.contract;

import org.springframework.util.MultiValueMap;

/**
 * Created by Tanmay.Patel on 1/17/2017.
 */
public interface HttpRestHandeler {
    String restfulServieceConsumer(String url, MultiValueMap parametersMap);
}
