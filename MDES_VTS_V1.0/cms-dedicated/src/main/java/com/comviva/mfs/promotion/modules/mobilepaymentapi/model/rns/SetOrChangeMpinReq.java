package com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 4/20/2017.
 */
@Getter
@Setter
public class SetOrChangeMpinReq {


        private String mobileKeysetId;
        private String authenticationCode;
        private String encryptedData;
        private String requestId;

    public SetOrChangeMpinReq(String mobileKeysetId, String authenticationCode, String encryptedData, String requestId) {
        this.mobileKeysetId = mobileKeysetId;
        this.authenticationCode = authenticationCode;
        this.encryptedData = encryptedData;
        this.requestId = requestId;
    }

    public SetOrChangeMpinReq() {

    }
}

