package com.comviva.hceservice.register;

import com.comviva.hceservice.common.RestResponse;


/**
 * Response object of register user.
 */
public class RegisterUserResponse extends RestResponse {
    private String activationCode;

    /**
     * Returns activation code to activate user.
     * @return  Activation Code
     */
    public String getActivationCode() {
        return activationCode;
    }

    /**
     * Set activation code.
     * @param activationCode    Activation code
     */
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

}
