package com.comviva.mfs.promotion.modules.card_management.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Getter
@ToString
@EqualsAndHashCode
public class AddCardResponse {
    private final Map response;

    public AddCardResponse(Map mapResponse) {
        this.response = mapResponse;
    }
}
