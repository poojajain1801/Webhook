package com.comviva.hceservice.digitizationApi;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */

public enum Boolean {
    Y,
    N;

    public static Boolean getBoolean(String value) {
        switch (value) {
            case "Y":
                return Y;

            case "N":
                return N;

            default:
                return null;
        }
    }
}
