package com.comviva.mfs.promotion.modules.credentialmanagement.model;

import com.comviva.mfs.promotion.model.CmsDResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Provisioning Response.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Getter
@Setter
@EqualsAndHashCode
public class ProvisionResponseMdes extends CmsDResponse {
    public ProvisionResponseMdes(String responseId, String responseHost, String reasonCode, String reasonDescription) {
        super(responseId, responseHost, reasonCode, reasonDescription);
    }

    public ProvisionResponseMdes() {
    }
}
