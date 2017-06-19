package com.comviva.hceservice.fcm;

import com.comviva.hceservice.common.ComvivaHce;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class ComvivaFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        /*ComvivaHce comvivaHce = ComvivaHce.getInstance(getApplicationContext());
        RnsInfo rnsInfo = comvivaHce.getRnsInfo();
        if (rnsInfo.getRegistrationId() == null) {
            // Getting registration token
            rnsInfo.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
            rnsInfo.setRnsType(RnsInfo.RNS_TYPE.FCM);
            comvivaHce.saveRnsInfo(rnsInfo);
        }*/
    }
}