package com.comviva.hceservice.register;


import com.comviva.hceservice.common.RestResponse;

public interface RegisterUserListener {
    void onRegistrationStarted();

    void onRegistrationCompeted();

    void onError();

    void setRegisterUserResponse(RegisterUserResponse registerUserResponse);
}
