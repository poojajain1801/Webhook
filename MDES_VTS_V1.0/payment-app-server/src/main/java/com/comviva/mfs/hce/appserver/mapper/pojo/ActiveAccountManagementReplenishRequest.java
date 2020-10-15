package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * ActiveAccountManagementReplenishRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveAccountManagementReplenishRequest {

    private String mac;
    private String api;
    private String sc;
    private List tvl;
    private String vprovisionedTokenID;
}