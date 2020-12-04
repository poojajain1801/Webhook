package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoTokenRequest;

import java.util.Map;

public interface RePersoService {
    Map<String, Object> rePersoTokenDataRequest(RePersoTokenRequest rePersoTokenRequest);
}
