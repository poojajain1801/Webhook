package com.comviva.hceservice.mdes.digitizatioApi;

import java.io.Serializable;

/**
 * Contains the Eligibility Receipt, provided by MDES check was successful. The client must provide
 * the Eligibility Receipt value back to MDES in the Digitize API to proceed with digitization.
 */
public class EligibilityReceipt implements Serializable {
    private String value;
    private int validForMinutes;

    /**
     * Returns Eligibility Receipt value.
     * @return Eligibility Receipt
     */
    public String getValue() {
        return value;
    }

    /**
     * Set Eligibility Receipt value.
     * @param value Eligibility Receipt value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns how long this Eligibility Receipt is valid for, in minutes.
     * @return Validity duration
     */
    public int getValidForMinutes() {
        return validForMinutes;
    }

    /**
     * Set validity duration of the receipt.
     * @param validForMinutes Validity duration
     */
    public void setValidForMinutes(int validForMinutes) {
        this.validForMinutes = validForMinutes;
    }
}
