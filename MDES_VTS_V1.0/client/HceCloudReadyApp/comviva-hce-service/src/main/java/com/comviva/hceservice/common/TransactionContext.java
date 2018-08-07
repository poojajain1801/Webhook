package com.comviva.hceservice.common;

import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;

public class TransactionContext {
    private double txnAmount;
    private long txnFirstTapTime;
    private CdCvm cdCvm;
    private boolean isFirstTap;

    public double getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(double txnAmount) {
        this.txnAmount = txnAmount;
    }

    public long getTxnFirstTapTime() {
        return txnFirstTapTime;
    }

    public void setTxnFirstTapTime(long txnFirstTapTime) {
        this.txnFirstTapTime = txnFirstTapTime;
    }

    public CdCvm getCdCvm() {
        return cdCvm;
    }

    public void setCdCvm(CdCvm cdCvm) {
        this.cdCvm = cdCvm;
    }

    public boolean isFirstTap() {
        return isFirstTap;
    }

    public void setFirstTap(boolean firstTap) {
        isFirstTap = firstTap;
    }

    public void resetTransactionContext() {
        cdCvm.setStatus(false);
        cdCvm.setType(Type.NONE);
        cdCvm.setEntity(Entity.NONE);
        isFirstTap = false;
        txnAmount = -1;
    }
}
