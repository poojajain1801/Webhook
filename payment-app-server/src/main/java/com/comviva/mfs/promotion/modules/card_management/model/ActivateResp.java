package com.comviva.mfs.promotion.modules.card_management.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Activate response
 * Created by tarkeshwar.v on 2/10/2017.
 */
@Getter
@Setter
public class ActivateResp {
    private String responseHost;
    private String responseId;
    private String result;
    private String reasonCode;
    private String reasonDescription;

    public ActivateResp(String responseHost, String responseId, String result, String reasonCode, String reasonDescription) {
        this.responseHost = responseHost;
        this.responseId = responseId;
        this.result = result;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    public ActivateResp() {
    }
}
