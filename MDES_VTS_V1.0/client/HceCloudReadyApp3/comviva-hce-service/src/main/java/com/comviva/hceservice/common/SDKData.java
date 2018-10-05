package com.comviva.hceservice.common;

import android.content.Context;

import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.internalSdkListeners.TransactionCompletionListener;
import com.comviva.hceservice.register.Registration;
import com.mastercard.mpsdk.interfaces.Mcbp;

/**
 * Created by amit.randhawa on 21-03-2018.
 */
public class SDKData {

    private static SDKData sdkData;
    private Mcbp mcbp;
    private Context context = null;
    private String imei = "";
    private boolean isCheckEligibilityPerformed = false;
    private boolean isEnrollPanPerformed = false;
    private Registration registration = null;
    private Digitization digitization = null;
    private ComvivaSdk comvivaSdk = null;
    private CardSelectionManagerForTransaction cardSelectionManagerForTransaction = null;
    // Payment Related variables
    private CdCvm cdCvm = null;
    private TransactionCompletionListener transactionCompletionListener = null;
    private PaymentCard selectedCard = null;
    private long txnFirstTapTime;
    private boolean isFirstTap;


    /**
     * Returns initialization state of the SDK.
     *
     * @return <code>true </code>If SDK is initialized
     * <code>false </code>SDK is not initialized
     */
    private SDKData() {
        //Intensionally Left Empty
    }


    public static SDKData getInstance() {

        if (sdkData == null) {
            sdkData = new SDKData();
        }
        return sdkData;
    }


    public PaymentCard getSelectedCard() {

        return selectedCard;
    }


    public void setSelectedCard(PaymentCard selectedCard) {

        this.selectedCard = selectedCard;
    }


    public void setInstanceNull() {

        sdkData = null;
        setDigitization(null);
        setRegistration(null);
        setComvivaSdk(null);
    }


    public boolean isCheckEligibilityPerformed() {

        return isCheckEligibilityPerformed;
    }


    public void setCheckEligibilityPerformed(boolean checkEligibilityPerformed) {

        isCheckEligibilityPerformed = checkEligibilityPerformed;
    }


    public boolean isEnrollPanPerformed() {

        return isEnrollPanPerformed;
    }


    public void setEnrollPanPerformed(boolean enrollPanPerformed) {

        isEnrollPanPerformed = enrollPanPerformed;
    }


    public static SDKData getSdkData() {

        return sdkData;
    }


    public static void setSdkData(SDKData sdkData) {

        SDKData.sdkData = sdkData;
    }


    public String getImei() {

        return imei;
    }


    public void setImei(String imei) {

        this.imei = imei;
    }


    public Context getContext() {

        return context;
    }


    public void setContext(Context context) {

        this.context = context;
    }


    public Mcbp getMcbp() {

        return mcbp;
    }


    public void setMcbp(Mcbp mcbpInstance) {

        mcbp = mcbpInstance;
    }


    public Registration getRegistration() {

        return registration;
    }


    public void setRegistration(Registration registration) {

        this.registration = registration;
    }


    public Digitization getDigitization() {

        return digitization;
    }


    public void setDigitization(Digitization digitization) {

        this.digitization = digitization;
    }


    public ComvivaSdk getComvivaSdk() {

        return comvivaSdk;
    }


    public void setComvivaSdk(ComvivaSdk comvivaSdk) {

        this.comvivaSdk = comvivaSdk;
    }


    public CardSelectionManagerForTransaction getCardSelectionManagerForTransaction() {

        return cardSelectionManagerForTransaction;
    }


    public void setCardSelectionManagerForTransaction(CardSelectionManagerForTransaction cardSelectionManagerForTransaction) {

        this.cardSelectionManagerForTransaction = cardSelectionManagerForTransaction;
    }


    public TransactionCompletionListener getTransactionCompletionListener() {

        return transactionCompletionListener;
    }


    public void setTransactionCompletionListener(TransactionCompletionListener transactionCompletionListener) {

        this.transactionCompletionListener = transactionCompletionListener;
    }


    public CdCvm getCdCvm() {

        return cdCvm;
    }


    public void setCdCvm(CdCvm cdCvm) {

        this.cdCvm = cdCvm;
    }


    public long getTxnFirstTapTime() {

        return txnFirstTapTime;
    }


    public void setTxnFirstTapTime(long txnFirstTapTime) {

        this.txnFirstTapTime = txnFirstTapTime;
    }


    public boolean isFirstTap() {

        return isFirstTap;
    }


    public void setFirstTap(boolean firstTap) {

        isFirstTap = firstTap;
    }


    public void resetTransactionData() {

        CdCvm cdCvm = getCdCvm();
        cdCvm.setStatus(false);
        cdCvm.setType(Type.NONE);
        cdCvm.setEntity(Entity.NONE);
        setCdCvm(cdCvm);
        setFirstTap(false);
    }
}
