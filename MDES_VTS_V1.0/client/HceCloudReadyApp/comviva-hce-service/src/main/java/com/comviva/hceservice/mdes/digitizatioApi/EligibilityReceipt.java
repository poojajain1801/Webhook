package com.comviva.hceservice.mdes.digitizatioApi;

import java.io.Serializable;

/**
 * Created by tarkeshwar.v on 5/26/2017.
 */
public class EligibilityReceipt implements Serializable {
    private String value;
    private int validForMinutes;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getValidForMinutes() {
        return validForMinutes;
    }

    public void setValidForMinutes(int validForMinutes) {
        this.validForMinutes = validForMinutes;
    }
}
