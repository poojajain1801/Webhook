package com.comviva.mfs.promotion.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all request to CMS-D.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Getter
@Setter
public class CmsDRequest {
    private String requestId;
    private String responseHost;

    public CmsDRequest(String requestId, String responseHost) {
        this.requestId = requestId;
        this.responseHost = responseHost;
    }

    public CmsDRequest() {
    }
}
