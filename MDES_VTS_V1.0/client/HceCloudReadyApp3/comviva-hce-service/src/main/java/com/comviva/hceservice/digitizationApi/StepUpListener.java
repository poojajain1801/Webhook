package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.util.ResponseListener;

/**
 * Created by amit.randhawa on 22-05-2018.
 */

 interface StepUpListener extends ResponseListener {
    public void onRequireAdditionalAuthentication(String panEnrollmentId, String provisionID, AuthenticationMethod[] authenticationMethods);
}
