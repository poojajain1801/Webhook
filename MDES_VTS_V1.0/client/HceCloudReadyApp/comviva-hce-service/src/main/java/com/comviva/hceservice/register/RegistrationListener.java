package com.comviva.hceservice.register;

import com.comviva.hceservice.common.CommonListener;

/**
 * UI listener for Register Device & Activate user.
 */
public interface RegistrationListener extends CommonListener {
    /**
     * Task is completed.
     */
    void onCompleted();

}
