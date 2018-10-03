package com.comviva.hceservice.common;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;

import java.util.Calendar;

public class CdCvmActivity extends Activity {

    public static final int REQ_CODE_SCREEN_LOCK = 0x9025;
    public SDKData sdkData;
    private boolean isAppInForeground = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ComvivaHceService.setIsPinpageRequired(false);
        sdkData = SDKData.getInstance();
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra(Tags.STATUS.getTag())){
            isAppInForeground = getIntent().getBooleanExtra(Tags.STATUS.getTag(),false);
        }
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        Intent screenLock = keyguardManager.createConfirmDeviceCredentialIntent("Card Holder Verification", "Please Verify");
        screenLock.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(screenLock, REQ_CODE_SCREEN_LOCK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SCREEN_LOCK:
                if (resultCode == Activity.RESULT_OK) {
                    CdCvm cdcvm = CdCvm.getInstance();
                    cdcvm.setEntity(Entity.MOBILE_APP);
                    cdcvm.setType(Type.PATTERN);
                    cdcvm.setStatus(true);
                    sdkData.setFirstTap(true);
                    sdkData.setTxnFirstTapTime(Calendar.getInstance().getTimeInMillis());
                    ComvivaHceService.setIsPinpageRequired(true);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    CdCvm cdcvm = CdCvm.getInstance();
                    cdcvm.setEntity(Entity.MOBILE_APP);
                    cdcvm.setType(Type.PATTERN);
                    cdcvm.setStatus(false);
                    sdkData.setFirstTap(false);
                    sdkData.setTxnFirstTapTime(Calendar.getInstance().getTimeInMillis());
                    ComvivaHceService.setIsPinpageRequired(true);
                }
                if (!isAppInForeground) {
                    moveTaskToBack(true);
                }
                break;
            default:
                break;
        }
        finish();
    }
}
