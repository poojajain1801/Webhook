package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by shadab.ali on 07-11-2017.
 */
@Getter
@Setter
public class GetTokenListRequest {
    private String userId;
    private String index;
    private String maxRecord;
}
