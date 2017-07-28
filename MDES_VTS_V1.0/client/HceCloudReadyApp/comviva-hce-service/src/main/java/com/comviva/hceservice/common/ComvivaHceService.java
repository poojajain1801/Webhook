package com.comviva.hceservice.common;


import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.mastercard.mcbp.card.McbpCard;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.ApduResponse;
import com.visa.cbp.sdk.facade.data.CvmMode;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.TokenStatus;
import com.visa.cbp.sdk.facade.data.VerifyingEntity;
import com.visa.cbp.sdk.facade.data.VerifyingType;
import com.visa.cbp.sdk.facade.error.CbpError;
import com.visa.cbp.sdk.facade.error.SDKErrorType;

public class ComvivaHceService {
    private static ComvivaHceService hceService;
    private PaymentCard paymentCard;
    private VisaPaymentSDK visaPaymentSDK;

    private static final String TAG = "VcpcsService";
    private byte[] CBP_ERROR_INVALID_CARD = {(byte) 0x69, (byte) 0x86};
    private byte[] CBP_ERROR_INVALID_CARD_STATUS = {(byte) 0x69, (byte) 0x85};

    private ComvivaHceService() {
    }

    private byte[] processMdes(byte[] commandApdu) {
        ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        McbpCard currentCard = (McbpCard) comvivaHce.getPaymentCard().getCurrentCard();
        return currentCard.processApdu(commandApdu);
    }

    private byte[] processVts(byte[] commandApdu, Bundle bundle) {
        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();

        // If there is no card selected, return error
        TokenKey selectedToken = visaPaymentSDK.getSelectedCard();

        if (selectedToken == null) {
            Log.e(TAG, "There is no card selected. " + "Transactions will not work until a card is selected.");
            return CBP_ERROR_INVALID_CARD;
        }

        // If token is not active, return error
        TokenStatus tokenStatus = TokenStatus.valueOf(visaPaymentSDK.getTokenStatus(selectedToken));
        if (tokenStatus != TokenStatus.ACTIVE) {
            return CBP_ERROR_INVALID_CARD_STATUS;
        }

        visaPaymentSDK.setCvmVerified(true);
        visaPaymentSDK.setCvmVerificationMode(new CvmMode(VerifyingEntity.MOBILE_APP, VerifyingType.PASSCODE));

        ApduResponse apduResponse = visaPaymentSDK.processCommandApdu(commandApdu, bundle, true);

        // Look for any errors piggybacking with the returned APDU bytes.
        CbpError cbpError = apduResponse != null ? apduResponse.getCbpError() : null;
        if (cbpError != null && cbpError.getErrorCode() == SDKErrorType.CVM_VERIFICATION_REQUIRED.getCode()) {
            Log.d(TAG, "CVM Required in VcpcsService");
            Log.d(TAG, "apduResponse.getApduData().length = " + apduResponse.getApduData().length + " \n cbp error - " + cbpError.getErrorCode() + " " + cbpError.getErrorMessage());
        }

        return apduResponse.getApduData();
    }

    public static ComvivaHceService getInstance() {
        if (hceService == null) {
            hceService = new ComvivaHceService();
        }
        return hceService;
    }

    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        paymentCard = comvivaHce.getPaymentCard();
        switch (paymentCard.getCardType()) {
            case MDES:
                return processMdes(commandApdu);

            case VTS:
                return processVts(commandApdu, extras);

            default:
                return new byte[]{0x69, (byte) 0x85};
        }
    }

    public void onDeactivated(int reason) {
        if(paymentCard == null) {
            return;
        }

        // Log Deactivation Error
        if (reason == HostApduService.DEACTIVATION_DESELECTED) {
            Log.d(TAG, "onDeactivated DEACTIVATION_DESELECTED");
        } else if (reason == HostApduService.DEACTIVATION_LINK_LOSS) {
            Log.d(TAG, "onDeactivated DEACTIVATION_LINK_LOSS");
        }

        switch (paymentCard.getCardType()) {
            case MDES:
                ((McbpCard)paymentCard.getCurrentCard()).processOnDeactivated();
                break;

            case VTS:
                visaPaymentSDK.processTransactionComplete(visaPaymentSDK.getSelectedCard());
                break;
        }
        paymentCard = null;
    }
}
