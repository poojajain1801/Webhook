package com.comviva.hceservice.register;

/**
 * UI listener for Register User.
 */
public interface RegisterUserListener {
    /**
     * User registration started
     */
    void onRegistrationStarted();

    /**
     * User registration successful.
     * @param registerUserResponse Register User Response
     */
    void onRegistrationCompeted(RegisterUserResponse registerUserResponse);

    /**
     * Error occurred while registering user.
     */
    void onError(String errorMessage);
}
