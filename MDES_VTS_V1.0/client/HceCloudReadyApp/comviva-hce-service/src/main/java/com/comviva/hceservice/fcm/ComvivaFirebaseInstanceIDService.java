package com.comviva.hceservice.fcm;

import com.comviva.hceservice.common.ComvivaHce;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class ComvivaFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
    }
}