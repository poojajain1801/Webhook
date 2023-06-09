package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Response for Get Asset API.
 * Created by tarkeshwar.v on 2/8/2017.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Asset {
    private final Map response;

    public Asset(Map response) {
        this.response = response;
    }
}
