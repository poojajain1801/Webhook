package com.comviva.hceservice.transactionHistory;

import android.util.Log;

import com.comviva.hceservice.apiCalls.NetworkApi;
import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.listeners.TransactionHistoryListener;
import com.comviva.hceservice.pojo.transactionhistorymdes.TransactionHistoryRegisterMdesResponse;
import com.comviva.hceservice.responseobject.transactionhistory.TransactionHistoryData;
import com.comviva.hceservice.util.Constants;

/**
 * This Class contains all transaction history related APIs.
 */
public class TransactionHistory implements ServerResponseListener {

    private static SDKData sdkData = SDKData.getInstance();
    private NetworkApi networkApi = new NetworkApi();
    private PaymentCard paymentCard;


    /**
     * This API is used to get the transaction History .
     *
     * @param paymentCard                Payment Card whose transaction history needs to be fetched.
     * @param count                      Number of records to retrieve. Maximum is 10. If not specified, the maximum number of records will be returned, up to 10, inclusive.
     * @param transactionHistoryListener UI Listener
     */
    public void getTransactionHistory(final PaymentCard paymentCard, final int count, final TransactionHistoryListener transactionHistoryListener) throws SdkException {

        transactionHistoryListener.onStarted();
        this.paymentCard = paymentCard;
        networkApi.setServerAuthenticateListener(this);
        if (CardType.MDES.equals(paymentCard.getCardType())) {
            if (null == CommonUtil.getSharedPreference(paymentCard.getCardUniqueId(), Constants.SHARED_PREF_MDES_CARD_STATUS_DETAILS) || CommonUtil.getSharedPreference(paymentCard.getCardUniqueId(), Constants.SHARED_PREF_MDES_CARD_STATUS_DETAILS).equals("I")) {
                transactionHistoryListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                networkApi.getRegisterTransactionHistoryMdes(paymentCard.getCardUniqueId());
                networkApi.setServerAuthenticateListener(this);
            } else {
                networkApi.getTransactionHistory(paymentCard, count, transactionHistoryListener);
            }
        } else {
            networkApi.getTransactionHistory(paymentCard, count, transactionHistoryListener);
        }
    }


    @Override
    public void onRequestCompleted(Object result, Object listener) {

        try {
            if (result instanceof TransactionHistoryData) {
                TransactionHistoryData transactionHistoryData = (TransactionHistoryData) result;
                TransactionHistoryListener transactionHistoryListener = (TransactionHistoryListener) listener;
                if (Constants.HTTP_RESPONSE_CODE_200.equals(transactionHistoryData.getResponseCode())) {
                    transactionHistoryListener.onSuccess(transactionHistoryData.getTransactionDetails());
                } else {
                    transactionHistoryListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(transactionHistoryData.getResponseCode()), transactionHistoryData.getResponseMessage()));
                }
            } else if (result instanceof TransactionHistoryRegisterMdesResponse) {
                TransactionHistoryRegisterMdesResponse transactionHistoryRegisterMdesResponse = (TransactionHistoryRegisterMdesResponse) result;
                if (Constants.HTTP_RESPONSE_CODE_200.equals(transactionHistoryRegisterMdesResponse.getResponseCode()) && null != transactionHistoryRegisterMdesResponse.getRegistrationStatus()) {
                    CommonUtil.setSharedPreference(paymentCard.getCardUniqueId(), transactionHistoryRegisterMdesResponse.getRegistrationStatus(), Constants.SHARED_PREF_MDES_CARD_STATUS_DETAILS);
                }
            }
        } catch (Exception e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
            CommonUtil.handleError(listener);
        }
    }


    @Override
    public void onRequestError(String message, Object listener) {

        CommonUtil.handleError(listener, message);
    }
}
