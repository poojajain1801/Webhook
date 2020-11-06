package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Activate response
 * Created by tarkeshwar.v on 2/10/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActivateResp {
    private String responseHost;
    private String responseId;
    private String result;
    private String reasonCode;
    private String reasonDescription;

}
