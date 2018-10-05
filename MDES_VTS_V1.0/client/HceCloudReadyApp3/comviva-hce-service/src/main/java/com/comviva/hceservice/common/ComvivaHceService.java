package com.comviva.hceservice.common;

import android.app.Application;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.DeviceLockUtil;
import com.comviva.hceservice.util.Miscellaneous;
import com.comviva.hceservice.util.TlvUtil;
import com.mastercard.mpsdk.componentinterface.RolloverInProgressException;
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
    private Application context;
    private int indexOfAmountInPdolData;
    private double transactionAmt;
    private ComvivaSdkInitData initData;
    private static boolean isPinpageRequired = true;
    private boolean pinRequiredFlag = false;
    private static final String NO_CURRENT_SECURITY = "Currently no security lock is implemented";
    private static final String TAG = "VcpcsService";
    private static final String PPSE_AID = "325041592E5359532E4444463031";
    private SDKData sdkData;
    private final byte[] INVALID_PARAMETER = new byte[]{0x69, (byte) 0x86};
    private final byte[] PAYMENT_INTERNAL_ERROR = new byte[]{0x6F, (byte) 0x00};
    private final byte[] PAYMENT_CONDITION_NOT_SATISFIED = new byte[]{0x69, (byte) 0x85};


    private ComvivaHceService(Application ctx) {

        this.context = ctx;
        try {
            sdkData = SDKData.getInstance();
            if (null == sdkData.getComvivaSdk()) {
                sdkData.setComvivaSdk(ComvivaSdk.getInstance(ctx));
            }
        } catch (Exception e) {
            Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
    }


    public static void setIsPinpageRequired(boolean isPinpageRequired) {

        ComvivaHceService.isPinpageRequired = isPinpageRequired;
    }


    public static boolean isIsPinpageRequired() {

        return isPinpageRequired;
    }


    private void showToast(final String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    private byte[] processMdes(final byte[] commandApdu, boolean isAppInForeground) {

        getCvmResetTimeout();
        switch (commandApdu[1] & 0xFF) {
            //  Select Command
            case 0xA4:
                break;
            //  GPO Command
            case 0xA8:
                break;
            //  Read Record Command
            case 0xB2:
                break;
            //  Generate SE Command
            case 0xAE:
                initData = sdkData.getComvivaSdk().getInitializationData();
                String amount = ArrayUtil.getHexString(Arrays.copyOfRange(commandApdu, 5, 11));
                transactionAmt = Double.parseDouble(amount) / 100;
                boolean isComvivaRequiredCDCVM = false;
                if (null != sdkData.getCdCvm()) {
                    isComvivaRequiredCDCVM = ((!sdkData.getCdCvm().isStatus()) && (initData.isHvtSupport()) && (transactionAmt > initData.getHvtLimit()));
                } else {
                    isComvivaRequiredCDCVM = ((initData.isHvtSupport()) && (transactionAmt > initData.getHvtLimit()));
                }
                if (isComvivaRequiredCDCVM) {
                    showCDCVMActivity(isAppInForeground);
                    return INVALID_PARAMETER;
                }
                break;
            default:
                break;
        }
        byte[] data = sdkData.getMcbp().getApduProcessor().processApdu(commandApdu);
        return data;
    }


    private byte[] processVts(byte[] commandApdu, Bundle bundle, boolean isAppInForeground) {

        getCvmResetTimeout();
        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        ApduResponse apduResponse = null;
        switch (commandApdu[1] & 0xFF) {
            //  Select Command
            case 0xA4:
                apduResponse = visaPaymentSDK.processCommandApdu(commandApdu, bundle, false);
                if (apduResponse.getApduData() != null) {
                    final String selectResponse = ArrayUtil.getHexString(apduResponse.getApduData());
                    Log.d("SelectCmdComvivvaSDK", selectResponse);
                    if (!selectResponse.contains(PPSE_AID)) {
                        int indexOfPdol = selectResponse.indexOf("9F38");
                        if (indexOfPdol != -1) {
                            indexOfPdol = indexOfPdol / 2;
                            Log.d("PDOLIndex", Integer.toString(indexOfPdol));
                            int pdolLength = apduResponse.getApduData()[indexOfPdol + 2] & 0xFF;
                            Log.d("PDOLLength", Integer.toString(pdolLength));
                            byte[] pdol = Arrays.copyOfRange(apduResponse.getApduData(), indexOfPdol + 3, indexOfPdol + 3 + pdolLength);
                            indexOfAmountInPdolData = TlvUtil.getAmountAuthIndex(pdol);
                            Log.d("indexOfAmountInPdolData", Integer.toString(indexOfAmountInPdolData));
                        }
                    }
                } else {
                    return INVALID_PARAMETER;
                }
                break;
            //  GPO Command
            case 0xA8:
                initData = sdkData.getComvivaSdk().getInitializationData();
                if (indexOfAmountInPdolData != -1) {
                    String amount = ArrayUtil.getHexString(Arrays.copyOfRange(commandApdu, 7 + indexOfAmountInPdolData, 7 + indexOfAmountInPdolData + 6));
                    transactionAmt = Double.parseDouble(amount) / 100;
                }
                //CDCVM
                boolean isComvivaRequiredCDCVM = false;
                if (null != sdkData.getCdCvm()) {
                    isComvivaRequiredCDCVM = ((!sdkData.getCdCvm().isStatus()) && (initData.isHvtSupport()) && (transactionAmt > initData.getHvtLimit()));
                } else {
                    isComvivaRequiredCDCVM = ((initData.isHvtSupport()) && (transactionAmt > initData.getHvtLimit()));
                }
                if (isComvivaRequiredCDCVM) {
                    showCDCVMActivity(isAppInForeground);
                    return INVALID_PARAMETER;
                }
                visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                if (sdkData.getCdCvm() != null) {
                    visaPaymentSDK.setCvmVerified(sdkData.getCdCvm().isStatus());
                    VerifyingEntity verifyingEntity;
                    VerifyingType verifyingType;
                    switch (sdkData.getCdCvm().getEntity()) {
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
                    switch (sdkData.getCdCvm().getType()) {
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
                } else {
                    visaPaymentSDK.setCvmVerified(false);
                }
                apduResponse = visaPaymentSDK.processCommandApdu(commandApdu, bundle, visaPaymentSDK.isCvmVerified());
                CbpError cbpError = apduResponse != null ? apduResponse.getCbpError() : null;
                //if error type equals root
                if (cbpError != null && cbpError.getErrorCode() == SDKErrorType.SUPER_USER_PERMISSION_DETECTED.getCode()) {
                    ComvivaSdk.reportFraud();
                    return INVALID_PARAMETER;
                }
                if (cbpError != null && cbpError.getErrorCode() == SDKErrorType.CVM_VERIFICATION_REQUIRED.getCode()) {
                    Log.d("CDCVM", "CVM Required in VcpcsService pos hvt threshold crossed");
                    Log.d("CDCVM", "apduResponse.getApduData().length = " + apduResponse.getApduData().length + " \n cbp error - " + cbpError.getErrorCode() + " " + cbpError.getErrorMessage());
                    if ((!sdkData.isFirstTap()) && (null == sdkData.getCdCvm() || sdkData.getCdCvm().getType() == Type.NONE || !sdkData.getCdCvm().isStatus())) {
                        showCDCVMActivity(isAppInForeground);
                        return INVALID_PARAMETER;
                    }
                }
                break;
            //  Read Record Command
            case 0xB2:
                apduResponse = visaPaymentSDK.processCommandApdu(commandApdu, bundle, visaPaymentSDK.isCvmVerified());
                break;
            //  Generate SE Command
            case 0xAE:
                break;
            default:
                break;
        }
        if(null != apduResponse){
            return apduResponse.getApduData();
        }else{
            return null;
        }

    }


    private void showCDCVMActivity(boolean isAppInForeground) {

        if (isIsPinpageRequired()) {
            if (DeviceLockUtil.checkLockingMech(context)) {
                Intent intent = new Intent(context, CdCvmActivity.class);
                intent.putExtra(Tags.STATUS.getTag(), isAppInForeground);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                pinRequiredFlag = true;
            } else {
                // No lock mechanism is set on device currently
                Log.d(Tags.DEBUG_LOG.getTag(), NO_CURRENT_SECURITY);
                PropertyReader propertyReader = PropertyReader.getInstance(context);
                String toastMessage = propertyReader.getToastMessage("NO_SCREEN_LOCK_SET");
                if (null != toastMessage) {
                    showToast(toastMessage);
                } else {
                    showToast("Please set device screen lock or fingerprint to make payment using Tap To Pay feature.");
                }
                pinRequiredFlag = false;
            }
        }
    }


    private void getCvmResetTimeout() {

        if (sdkData.isFirstTap()) {
            long cdCdvmResetTime = (Calendar.getInstance().getTimeInMillis() - sdkData.getTxnFirstTapTime()) / 1000;
            Log.d("cdCdvmResetTime", String.valueOf(cdCdvmResetTime));
            if (cdCdvmResetTime > sdkData.getSelectedCard().getCvmResetTimeout()) {
                sdkData.resetTransactionData();
            }
        }
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
        isAppInForeground = Miscellaneous.isAppOnForeground(context);
        Log.d(Tags.DEBUG_LOG.getTag(), "isAppInForeground" + isAppInForeground);
        // Application is in Foreground
        if (isAppInForeground) {
            paymentCard = sdkData.getSelectedCard();
        } else {
            // Application is in Background
            paymentCard = sdkData.getComvivaSdk().getDefaultPaymentCard();
        }
        // Check that there is no card selected or no default card card is set
        if (paymentCard == null) {
            Log.d(Tags.DEBUG_LOG.getTag(), Constants.PAYMENT_CARD_NULL);
            Log.e(TAG, "There is no card selected. " + "Transactions will not work until a card is selected.");
            return PAYMENT_CONDITION_NOT_SATISFIED;
        } else {
            Log.d("Payment Card", "" + paymentCard.getCardLast4Digit());
        }
        // If card is not in active state, send error status word
        switch (paymentCard.getCardState()) {
            case SUSPENDED:
            case INACTIVE:
                Log.e(TAG, "Card is Suspended/Inactive");
                showToast("Sorry, Card " + paymentCard.getCardLast4Digit() + " is Suspended/Inactive");
                return PAYMENT_CONDITION_NOT_SATISFIED;
            default:
                break;
        }
        if (paymentCard != null) {
            try {
                sdkData.getCardSelectionManagerForTransaction().setPaymentCardForTransaction(paymentCard.getCardType(), paymentCard);
            } catch (RolloverInProgressException e) {
                return PAYMENT_INTERNAL_ERROR;
            }
        }
        switch (paymentCard.getCardType()) {
            case MDES:
                return processMdes(commandApdu, isAppInForeground);
            case VTS:
                return processVts(commandApdu, extras, isAppInForeground);
            default:
                return PAYMENT_CONDITION_NOT_SATISFIED;
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
                    sdkData.getMcbp().getApduProcessor().processOnDeactivated();
                    // In Second Tap, if Transaction Fails, Card should be reset.
                    if (isPinpageRequired && sdkData.isFirstTap()) {
                        sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
                    }
                    // In Second Tap, if Transaction Fails, Card should be reset.
                    if (sdkData.getTransactionCompletionListener().isTxnSuccess()) {
                        sdkData.resetTransactionData();
                        sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
                        sdkData.getTransactionCompletionListener().setTxnSuccess(false);
                    }
                    sdkData.getCdCvm().setStatus(false);
                    break;
                case VTS:
                    indexOfAmountInPdolData = -1;
                    TokenKey tokenKey = ((TokenData) paymentCard.getCurrentCard()).getTokenKey();
                    boolean isTxnSuccess = visaPaymentSDK.processTransactionComplete(tokenKey);
                    ProcessContactlessListener processContactlessListener = sdkData.getTransactionCompletionListener().getProcessContactlessListener();
                    if (isTxnSuccess) {
                        sdkData.getComvivaSdk().consumeLuk(paymentCard);
                        sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
                        if (processContactlessListener != null) {
                            processContactlessListener.onContactlessPaymentCompleted(null);
                        }
                    } else {
                        if (processContactlessListener != null) {
                            if (pinRequiredFlag) {
                                processContactlessListener.onPinRequired(null);
                            } else {
                                processContactlessListener.onContactlessPaymentAborted(null);
                            }
                        }
                    }
                    // If this is second tap, reset transaction context
                    if (isTxnSuccess && sdkData.isFirstTap()) {
                        sdkData.resetTransactionData();
                    }
                    if (isPinpageRequired && sdkData.isFirstTap()) {
                        sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
                    }
                    if (sdkData.getCdCvm() != null) {
                        sdkData.getCdCvm().setEntity(Entity.NONE);
                        sdkData.getCdCvm().setType(Type.NONE);
                        sdkData.getCdCvm().setStatus(false);
                    }
                    visaPaymentSDK.setCvmVerified(false);
                    visaPaymentSDK.setCvmVerificationMode(new CvmMode(VerifyingEntity.NO_CD_CVM, VerifyingType.NO_CD_CVM));
                    break;
                default:
                    break;
            }
        } catch (RootDetectException e) {
            sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
            ComvivaSdk.reportFraud();
            Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
        } catch (Exception e) {
            sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
            Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
        pinRequiredFlag = false;
        transactionAmt = -1;
        paymentCard = null;
    }
}
