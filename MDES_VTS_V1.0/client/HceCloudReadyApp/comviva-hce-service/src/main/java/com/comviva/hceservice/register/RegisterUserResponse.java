package com.comviva.hceservice.register;

import com.comviva.hceservice.common.RestResponse;

/**
 * Created by tarkeshwar.v on 3/9/2017.
 */

public class RegisterUserResponse extends RestResponse {
    private String activationCode;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

}
