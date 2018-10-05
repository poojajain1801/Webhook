package com.comviva.hceservice.internalSdkListeners;

import com.comviva.hceservice.common.ComvivaHceService;
import com.comviva.hceservice.common.ProcessContactlessListener;
import com.comviva.hceservice.common.SDKData;
import com.mastercard.mchipengine.walletinterface.walletcommonenumeration.AbortReason;
import com.mastercard.mchipengine.walletinterface.walletdatatypes.ContactlessLog;
import com.mastercard.mpsdk.componentinterface.Card;
import com.mastercard.mpsdk.interfaces.TransactionEventListener;

public class TransactionCompletionListener implements TransactionEventListener {

    private ProcessContactlessListener processContactlessListener;
    private boolean isTxnSuccess = false;


    @Override
    public void onContactlessPaymentCompleted(Card card, ContactlessLog contactlessLog) {

        setTxnSuccess(true);
        if (getProcessContactlessListener() != null) {
            getProcessContactlessListener().onContactlessPaymentCompleted(null);
        }
    }


    @Override
    public void onContactlessPaymentIncident(Card card, Exception e) {

    }


    @Override
    public void onContactlessPaymentAborted(Card card, AbortReason abortReason, Exception e) {

        if (getProcessContactlessListener() != null) {
            getProcessContactlessListener().onContactlessPaymentAborted(null);
        }
    }


    @Override
    public void onTransactionStopped() {

        /*if (getProcessContactlessListener() != null) {
            getProcessContactlessListener().onContactlessPaymentAborted(null);
        }*/
    }


    public void setTxnSuccess(boolean txnSuccess) {

        isTxnSuccess = txnSuccess;
    }


    public boolean isTxnSuccess() {

        return isTxnSuccess;
    }


    public ProcessContactlessListener getProcessContactlessListener() {

        return processContactlessListener;
    }


    public void setProcessContactlessListener(ProcessContactlessListener processContactlessListener) {

        this.processContactlessListener = processContactlessListener;
    }
}
