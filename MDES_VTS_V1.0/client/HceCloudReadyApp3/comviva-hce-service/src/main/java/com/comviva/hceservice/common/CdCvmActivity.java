package com.comviva.hceservice.common;


import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;

import java.util.Calendar;

public class CdCvmActivity extends Activity {
    public static final int REQ_CODE_SCREEN_LOCK = 0x9025;
   /* PowerManager.WakeLock wl;
    PowerManager pm;*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        Intent screenLock = keyguardManager.createConfirmDeviceCredentialIntent("Card Holder Verification", "Please Verify");
        /* pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();*/
        startActivityForResult(screenLock, REQ_CODE_SCREEN_LOCK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SCREEN_LOCK:
                if(resultCode == Activity.RESULT_OK){
                CdCvm cdcvm = new CdCvm();
                cdcvm.setEntity(Entity.MOBILE_APP);
                cdcvm.setType(Type.PATTERN);
                cdcvm.setStatus(true);

                TransactionContext txnCtx = ComvivaHceService.getTransactionContext();
                txnCtx.setCdCvm(cdcvm);
                txnCtx.setFirstTap(true);
                txnCtx.setTxnFirstTapTime(Calendar.getInstance().getTimeInMillis());
                ComvivaHceService.setTransactionContext(txnCtx);
               /* wl.release();*/
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    CdCvm cdcvm = new CdCvm();
                    cdcvm.setEntity(Entity.MOBILE_APP);
                    cdcvm.setType(Type.PATTERN);
                    cdcvm.setStatus(false);

                    TransactionContext txnCtx = ComvivaHceService.getTransactionContext();
                    txnCtx.setCdCvm(cdcvm);
                    txnCtx.setFirstTap(false);
                    txnCtx.setTxnFirstTapTime(Calendar.getInstance().getTimeInMillis());
                    ComvivaHceService.setTransactionContext(txnCtx);
                   /* wl.release();*/


                }
                break;
        }
        finish();

    }
}
