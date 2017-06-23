package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Listener for Check Card Eligibility API.
 * Created by tarkeshwar.v on 5/24/2017.
 */

public interface CheckCardEligibilityListener {
    void onCheckEligibilityStarted();

    void onCheckEligibilityCompleted();

    void onCheckEligibilityError(String message);

    void onTermsAndConditionsRequired(CardEligibilityResponse cardEligibilityResponse);
}
