package com.comviva.hceservice.digitizationApi;

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
     * @param contentGuid Asset for Terms & Conditions
     */
    void onTermsAndConditionsRequired(ContentGuid contentGuid);
}
