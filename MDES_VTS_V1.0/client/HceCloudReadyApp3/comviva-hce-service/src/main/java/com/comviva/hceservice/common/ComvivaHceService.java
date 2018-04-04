package com.comviva.hceservice.common;


import android.app.Application;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.DeviceLockUtil;
import com.comviva.hceservice.util.Miscellaneous;
import com.comviva.hceservice.util.TlvUtil;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.listeners.ProcessContactlessListener;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.ApduResponse;
import com.visa.cbp.sdk.facade.data.CvmMode;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.VerifyingEntity;
import com.visa.cbp.sdk.facade.data.VerifyingType;
import com.visa.cbp.sdk.facade.error.CbpError;
import com.visa.cbp.sdk.facade.error.SDKErrorType;
import com.visa.cbp.sdk.facade.exception.RootDetectException;

import java.util.Arrays;
import java.util.Calendar;

public class ComvivaHceService {
    private static ComvivaHceService hceService;
    private PaymentCard paymentCard;
    private VisaPaymentSDK visaPaymentSDK;
    private boolean isCdCvmStatusSet;
    private Application context;
    private int indexOfAmountInPdolData;
    private double transactionAmt;
    private ComvivaSdk comvivaSdk;
    private ComvivaSdkInitData initData;
    private boolean isGpoReceived = false;
    private static TransactionContext txnCtx;

    public static boolean isIsPinpageRequired() {
        return isPinpageRequired;
    }

    public static void setIsPinpageRequired(boolean isPinpageRequired) {
        ComvivaHceService.isPinpageRequired = isPinpageRequired;
    }

    private static boolean isPinpageRequired = true;
    private boolean pinRequiredFlag = false ;
    private static final String COMVIVA_SDK_ERROR = "comvivaSdkError";
    private static final String  CDCVM_ERROR = "CdCvm Error";
    private static final String NO_CURRENT_SECURITY = "Currently no security lock is implemented";


    private static final String TAG = "VcpcsService";
    private static final String PPSE_AID = "325041592E5359532E4444463031";

    /**
     * CDCVM reset timeout
     */
    private static final int CDCVM_RESET_TIMEOUT_TIME = 30;

    private ComvivaHceService(Application ctx) {
        this.context = ctx;
        try {
            comvivaSdk = ComvivaSdk.getInstance(ctx);
            txnCtx = new TransactionContext();
        } catch (SdkException e) {
            Log.d(COMVIVA_SDK_ERROR, e.getMessage());
        }
    }


    private void showToast(final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private byte[] processMdes(byte[] commandApdu) {
        McbpCard currentCard = (McbpCard) comvivaSdk.getSelectedCard();
        return currentCard.processApdu(commandApdu);
    }

    private byte[] processVts(byte[] commandApdu, Bundle bundle, boolean isAppInForeground) {
        try {
            CdCvm cdCvm;
            // Reset Transaction Context if duration between 1st tap and 2nd tap is exceeded
            if (txnCtx.isFirstTap()) {
                long cdCdvmResetTime = (Calendar.getInstance().getTimeInMillis() - txnCtx.getTxnFirstTapTime()) / 1000;
                Log.d("cdCdvmResetTime", String.valueOf(cdCdvmResetTime));
                if (cdCdvmResetTime > CDCVM_RESET_TIMEOUT_TIME) {
                    txnCtx.resetTransactionContext();
                }

            }

            switch (commandApdu[1] & 0xFF) {
                // GPO
                case 0xA8:
                    isGpoReceived = true;
                    Log.d("GPOComvivaSdk", "Received");
                    if (indexOfAmountInPdolData != -1) {
                        String amount = ArrayUtil.getHexString(Arrays.copyOfRange(commandApdu, 7 + indexOfAmountInPdolData, 7 + indexOfAmountInPdolData + 6));
                        transactionAmt = Double.parseDouble(amount) / 100;
                        txnCtx.setTxnAmount(transactionAmt);
                        Log.d("TxnAmt", Double.toString(transactionAmt));
                    }
                    comvivaSdk.consumeLuk(paymentCard);
                    break;
                    default:
                        break;
            }


            if (isAppInForeground && comvivaSdk.getSelectedCard() != null) {
                cdCvm = paymentCard.getCdCvm();
            } else {
                cdCvm = txnCtx.getCdCvm();
            }

            if (txnCtx.isFirstTap() && isAppInForeground && comvivaSdk.getSelectedCard() != null ) {
                    cdCvm = txnCtx.getCdCvm();
            }
            visaPaymentSDK = VisaPaymentSDKImpl.getInstance();

            if (!isCdCvmStatusSet) {
                if (cdCvm != null) {
                    visaPaymentSDK.setCvmVerified(cdCvm.isStatus());
                    VerifyingEntity verifyingEntity;
                    VerifyingType verifyingType;

                    switch (cdCvm.getEntity()) {
                        case MOBILE_APP:
                            verifyingEntity = VerifyingEntity.MOBILE_APP;
                            break;

                        case VERIFIED_CLOUD:
                            verifyingEntity = VerifyingEntity.VERIFIED_CLOUD;
                            break;

                        case VERIFIED_MOBILE_DEVICE:
                            verifyingEntity = VerifyingEntity.VERIFIED_MOBILE_DEVICE;
                            break;

                        default:
                            verifyingEntity = VerifyingEntity.NO_CD_CVM;
                    }

                    switch (cdCvm.getType()) {
                        case PASSCODE:
                            verifyingType = VerifyingType.PASSCODE;
                            break;

                        case BIO_FINGERPRINT:
                            verifyingType = VerifyingType.FINGER_BIOMETRIC;
                            break;

                        case PATTERN:
                            verifyingType = VerifyingType.MOBILE_DEVICE_PATTERN_LOCK;
                            break;

                        default:
                            verifyingType = VerifyingType.NO_CD_CVM;
                    }
                    visaPaymentSDK.setCvmVerificationMode(new CvmMode(verifyingEntity, verifyingType));
                    isCdCvmStatusSet = true;
                } else {
                    visaPaymentSDK.setCvmVerified(false);
                }


                initData = comvivaSdk.getInitializationData();
            }

            // High Value transaction
            boolean isComvivaRequiredCDCVM = (initData.isHvtSupport() && isGpoReceived && !txnCtx.isFirstTap() && transactionAmt > initData.getHvtLimit());
            if (isGpoReceived) {
                Log.d("isComvivaRequiredCDCVM ", String.valueOf(isComvivaRequiredCDCVM));
                Log.d("CdcvmStatusIn Visa = ", String.valueOf(visaPaymentSDK.isCvmVerified()));
            }



                // No CDCVM verified
                if ((isComvivaRequiredCDCVM) &&( cdCvm == null || cdCvm.getType() == Type.NONE || !cdCvm.isStatus())) {
                    if (isPinpageRequired) {
                        if (DeviceLockUtil.checkLockingMech(context)) {
                            Intent intent = new Intent(context, CdCvmActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            pinRequiredFlag = true;
                            return new byte[]{0x69, (byte) 0x86};
                        } else {
                            // No lock mechanism is set on device currently
                            Log.d(CDCVM_ERROR, NO_CURRENT_SECURITY);
                            showToast("Screen Lock is not enabled on your mobile device. Please set up screenlock or Touch ID for enhanced security.");
                            pinRequiredFlag = false;
                            return new byte[]{0x69, (byte) 0x86};
                        }
                    } else {
                        Log.d(CDCVM_ERROR, "PIn page is already opend no need to open again");
                        return new byte[]{0x69, (byte) 0x86};
                    }
                }

            Log.d("****CDCVM in VISA******", String.valueOf(visaPaymentSDK.isCvmVerified()));
            ApduResponse apduResponse = visaPaymentSDK.processCommandApdu(commandApdu, bundle, visaPaymentSDK.isCvmVerified());

            // Look for any errors piggybacking with the returned APDU bytes.
            CbpError cbpError = apduResponse != null ? apduResponse.getCbpError() : null;
            //if error type equals root


            if (cbpError != null && cbpError.getErrorCode() == SDKErrorType.SUPER_USER_PERMISSION_DETECTED.getCode()) {
                ComvivaSdk.reportFraud();
                return new byte[]{0x69, (byte) 0x86};

            }

            if (cbpError != null && cbpError.getErrorCode() == SDKErrorType.CVM_VERIFICATION_REQUIRED.getCode()) {
                Log.d("CDCVM", "CVM Required in VcpcsService pos hvt threshold crossed");
                Log.d("CDCVM", "apduResponse.getApduData().length = " + apduResponse.getApduData().length + " \n cbp error - " + cbpError.getErrorCode() + " " + cbpError.getErrorMessage());
                if ((isGpoReceived && !txnCtx.isFirstTap()) && cdCvm == null || cdCvm.getType() == Type.NONE || !cdCvm.isStatus()) {
                        if (isPinpageRequired) {
                            if (DeviceLockUtil.checkLockingMech(context)) {
                                Intent intent = new Intent(context, CdCvmActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                pinRequiredFlag = true;
                                return new byte[]{0x69, (byte) 0x86};
                            } else {
                                // No lock mechanism is set on device currently
                                Log.d(CDCVM_ERROR, NO_CURRENT_SECURITY);
                                showToast("Screen Lock is not enabled on your mobile device. Please set up screenlock or Touch ID for enhanced security.");
                                pinRequiredFlag = false;
                                return new byte[]{0x69, (byte) 0x86};
                            }
                        } else {
                            Log.d(CDCVM_ERROR, "PIn page is already opend no need to open again");
                            return new byte[]{0x69, (byte) 0x86};
                        }
                }
            }

            byte[] responseApdu = null;
            if(null != apduResponse) {
                responseApdu  = apduResponse.getApduData();
            }else
            {
                return new byte[]{0x69, (byte) 0x86};
            }
            switch (commandApdu[1] & 0xFF) {
                // Select APDU
                case 0xA4:
                    isGpoReceived = false;
                    final String strSelResp = ArrayUtil.getHexString(responseApdu);
                    Log.d("SelectCmdComvivvaSDK", strSelResp);
                    if (!strSelResp.contains(PPSE_AID)) {
                        int indexOfPdol = strSelResp.indexOf("9F38");
                        if (indexOfPdol != -1) {
                            indexOfPdol = indexOfPdol / 2;
                            Log.d("PDOLIndex", Integer.toString(indexOfPdol));
                            int pdolLength = responseApdu[indexOfPdol + 2] & 0xFF;
                            Log.d("PDOLLength", Integer.toString(pdolLength));
                            byte[] pdol = Arrays.copyOfRange(responseApdu, indexOfPdol + 3, indexOfPdol + 3 + pdolLength);
                            indexOfAmountInPdolData = TlvUtil.getAmountAuthIndex(pdol);
                            Log.d("indexOfAmountInPdolData", Integer.toString(indexOfAmountInPdolData));
                        }
                    }
                    break;
                    default:
                        break;
            }
            return responseApdu;
        }catch (Exception e)
        {
            return new byte[]{0x69, (byte) 0x86};
        }


    }

    static void setTransactionContext(TransactionContext txnCtx) {
        ComvivaHceService.txnCtx = txnCtx;
    }

    static TransactionContext getTransactionContext() {
        return txnCtx;
    }

    /**
     * Provides Singleton instance of this class.
     *
     * @return Instance of ComvivaHceService
     */
    public static ComvivaHceService getInstance(Application context) {
        if (hceService == null) {
            hceService = new ComvivaHceService(context);
        }
        return hceService;
    }

    /**
     * Invoke this method when APDU is received from POS.
     *
     * @param commandApdu Command APDU object
     * @param extras      A bundle containing extra data. May be null.
     * @return Response APDU
     */
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        boolean isAppInForeground;
        try {
            if (comvivaSdk == null) {
                comvivaSdk = ComvivaSdk.getInstance(context);
            }

            isAppInForeground = Miscellaneous.isAppOnForeground(context);
            Log.d("App Foreground status" , "" +isAppInForeground);
            // Application is in Foreground
            if (isAppInForeground) {
                paymentCard = comvivaSdk.getSelectedCard();
            } else {
                // Application is in Background
                paymentCard = comvivaSdk.getDefaultPaymentCard();
                if (paymentCard!=null) {
                    comvivaSdk.setSelectedCard(paymentCard);
                }

            }
        } catch (SdkException e) {
            return new byte[]{0x6F, 0x00};
        }
        if(paymentCard == null)
        {
            Log.d("Payment Card" , "Null"  );
        }else
        {
            Log.d("Payment Card" , "" +paymentCard.getCardLast4Digit());
        }

        // Check that there is no card selected or no default card card is set
        if (paymentCard == null) {
            Log.e(TAG, "There is no card selected. " + "Transactions will not work until a card is selected.");
            return new byte[]{0x69, (byte) 0x85};
        }

        // If card is not in active state, send error status word
        switch (paymentCard.getCardState()) {
            case SUSPENDED:
            case UNINITIALIZED:
                Log.e(TAG, "Card is Suspended/Inactive");
                showToast("Sorry, Card " + paymentCard.getCardLast4Digit() + " is Suspended/Inactive");
                return new byte[]{0x69, (byte) 0x85};
                default:
                    break;
        }

        switch (paymentCard.getCardType()) {
            case MDES:
                return processMdes(commandApdu);

            case VTS:
                return processVts(commandApdu, extras, isAppInForeground);

            default:
                return new byte[]{0x69, (byte) 0x85};
        }
    }

    /**
     * Invoked on card deactivation or phone is removed from proximity field of POS.
     *
     * @param reason Reason Code
     */
    public void onDeactivated(int reason) {
        if (paymentCard == null) {
            return;
        }

        // Log Deactivation Error
        if (reason == HostApduService.DEACTIVATION_DESELECTED) {
            Log.d(TAG, "onDeactivated DEACTIVATION_DESELECTED");
        } else if (reason == HostApduService.DEACTIVATION_LINK_LOSS) {
            Log.d(TAG, "onDeactivated DEACTIVATION_LINK_LOSS");
        }

        try {
            switch (paymentCard.getCardType()) {
                case MDES:
                    ((McbpCard) paymentCard.getCurrentCard()).processOnDeactivated();
                    break;

                case VTS:
                    isCdCvmStatusSet = false;
                    indexOfAmountInPdolData = -1;
                    TokenKey tokenKey = ((TokenData) paymentCard.getCurrentCard()).getTokenKey();

                    boolean isTxnSuccess = visaPaymentSDK.processTransactionComplete(tokenKey);
                    ProcessContactlessListener processContactlessListener = paymentCard.getProcessContactlessListener();
                    if (isTxnSuccess) {
                        comvivaSdk.deSelectCard();
                        if (processContactlessListener != null) {
                            processContactlessListener.onContactlessPaymentCompleted(null);
                        }
                    } else {
                        if (processContactlessListener != null) {
                            if(pinRequiredFlag)
                            {
                                processContactlessListener.onPinRequired(null);
                            }else {
                                processContactlessListener.onContactlessPaymentAborted(null);
                            }

                        }
                    }

                    // If this is second tap, reset transaction context
                    if(isTxnSuccess && txnCtx.isFirstTap()) {
                        txnCtx.resetTransactionContext();
                    }
                    if (isPinpageRequired && txnCtx.isFirstTap())
                    {
                        comvivaSdk.deSelectCard();
                    }
                    CdCvm cdcvm = paymentCard.getCdCvm();
                    if (cdcvm!=null) {
                        cdcvm.setEntity(Entity.NONE);
                        cdcvm.setType(Type.NONE);
                        cdcvm.setStatus(false);
                    }

                    visaPaymentSDK.setCvmVerified(false);
                    visaPaymentSDK.setCvmVerificationMode(new CvmMode(VerifyingEntity.NO_CD_CVM, VerifyingType.NO_CD_CVM));

                    break;

                    default:
                        break;
            }
        } catch (RootDetectException e){
            comvivaSdk.deSelectCard();
            ComvivaSdk.reportFraud();
            Log.d(COMVIVA_SDK_ERROR, e.getMessage());
        }catch (Exception e) {
            comvivaSdk.deSelectCard();
            Log.d(COMVIVA_SDK_ERROR, e.getMessage());
        }
        pinRequiredFlag = false;
        transactionAmt = -1;
        paymentCard = null;
    }
}