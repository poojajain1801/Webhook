package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Created by tarkeshwar.v on 6/6/2017.
 */
public interface CardLcmListener {
    void onCardLcmStarted();

    void onSuccess(String message);

    void onError(String message);
}
