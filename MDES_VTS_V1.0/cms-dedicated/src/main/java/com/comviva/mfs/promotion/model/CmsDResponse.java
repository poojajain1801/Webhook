package com.comviva.mfs.promotion.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Base CMS-D response.
 * Created by tarkeshwar.v on 2/3/2017.
 */
@Getter
@Setter
public class CmsDResponse {
    private String responseId;
    private String responseHost;
    private String reasonCode;
    private String reasonDescription;

    public CmsDResponse(String responseId, String responseHost, String reasonCode, String reasonDescription) {
        this.responseId = responseId;
        this.responseHost = responseHost;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    public CmsDResponse() {
    }
}
