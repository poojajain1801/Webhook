package com.comviva.mfs.promotion.modules.mpamanagement.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public class DeviceRegisterResp {
    private final Map response;

    public DeviceRegisterResp(Map response) {
        this.response = response;
    }
}
