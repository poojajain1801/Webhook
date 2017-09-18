package com.comviva.hceservice.register;

import com.comviva.hceservice.common.CommonListener;

/**
 * UI listener for Register User.
 */
public interface RegisterUserListener extends CommonListener {
    /**
     * User registration successful.
     */
    void onRegistrationCompeted();
}
