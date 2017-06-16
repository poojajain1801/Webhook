package com.comviva.mfs.promotion.modules.mobilepaymentapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Response object for request session API.
 * Created by tarkeshwar.v on 2/13/2017.
 */
@Getter
@Setter
public class RequestSessionResp {
    private String reasonCode;
    private String reasonDescription;

    public RequestSessionResp(String reasonCode, String reasonDescription) {
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }
}
