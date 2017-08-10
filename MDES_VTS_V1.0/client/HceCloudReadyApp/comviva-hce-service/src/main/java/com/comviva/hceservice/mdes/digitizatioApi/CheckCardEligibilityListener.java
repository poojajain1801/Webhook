package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Listener for Check Card Eligibility API.
 */
public interface CheckCardEligibilityListener {
    /**
     * Check Card Eligibility is started.
     */
    void onCheckEligibilityStarted();

    /**
     * Check Eligibility performed successfully.
     */
    void onCheckEligibilityCompleted();

    /**
     * Error Occurred.
     * @param message Error message
     */
    void onCheckEligibilityError(String message);

    /**
     * Terms & Condition is required.
     * @param cardEligibilityResponse Card Eligibility Response
     */
    void onTermsAndConditionsRequired(CardEligibilityResponse cardEligibilityResponse);
}
