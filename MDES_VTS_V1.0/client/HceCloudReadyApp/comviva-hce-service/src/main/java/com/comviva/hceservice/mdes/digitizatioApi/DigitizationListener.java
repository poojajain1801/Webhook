package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Created by tarkeshwar.v on 5/26/2017.
 */
public interface DigitizationListener {
    void onDigitizationStarted();
    void onDigitizationCompleted();
    void onError(String message);
    void onApproved();
    void onDeclined();
    void onRequireAdditionalAuthentication();
}
