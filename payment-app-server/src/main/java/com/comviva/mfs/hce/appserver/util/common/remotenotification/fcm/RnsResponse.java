package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

import lombok.Getter;
import lombok.Setter;

/**
 * Response of RNS message.
 * Created by tarkeshwar.v on 2/14/2017.
 */
@Getter
@Setter
public class RnsResponse {
    private String errorCode;
    private String response;

    public RnsResponse(String errorCode, String response) {
        this.errorCode = errorCode;
        this.response = response;
    }

    public RnsResponse() {
    }
}
