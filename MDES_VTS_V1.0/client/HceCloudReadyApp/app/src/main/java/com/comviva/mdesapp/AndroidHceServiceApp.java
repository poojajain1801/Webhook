package com.comviva.mdesapp;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaHceService;

public class AndroidHceServiceApp extends HostApduService {
    public static final String TAG = "HostApduService";

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        ComvivaHceService comvivaHceService = ComvivaHceService.getInstance();
        byte[] response = comvivaHceService.processCommandApdu(commandApdu, extras);
        if (response == null) {
            return new byte[]{0x69, (byte) 0x85};
        }
        return response;
    }

    @Override
    public void onDeactivated(int reason) {
        if (reason == DEACTIVATION_DESELECTED) {
            Log.d(TAG, "onDeactivated DEACTIVATION_DESELECTED");
        } else if (reason == DEACTIVATION_LINK_LOSS) {
            Log.d(TAG, "onDeactivated DEACTIVATION_LINK_LOSS");
        }
        ComvivaHceService comvivaHceService = ComvivaHceService.getInstance();
        comvivaHceService.onDeactivated(reason);
    }
}
