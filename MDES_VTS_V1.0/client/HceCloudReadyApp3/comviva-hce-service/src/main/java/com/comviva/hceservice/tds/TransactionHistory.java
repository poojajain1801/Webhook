package com.comviva.hceservice.tds;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.ComvivaWalletListener;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mcbp.api.McbpCardApi;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This Class contains all transaction history related APIs.
 */
public class TransactionHistory {
    private static ArrayList<TransactionDetails> parseTransactionHistoryData(JSONObject jsTransactionHistory) throws JSONException {
        JSONArray transactionDetailsArray = jsTransactionHistory.getJSONArray("transactionDetails");
        ArrayList<TransactionDetails> transactionDetails = new ArrayList<>();
        TransactionDetails txnDetail;

        JSONObject jsTxnHistoryRec;
        final int noOfTxnHistory = transactionDetailsArray.length();
        for (int i = 0; i < noOfTxnHistory; i++) {
            jsTxnHistoryRec = new JSONObject(transactionDetailsArray.getJSONObject(i).getString("txnHistory"));

            txnDetail = new TransactionDetails();
            if(jsTxnHistoryRec.has("vProvisionedTokenID")) {
                String vProvisionedTokenId = jsTxnHistoryRec.getString("vProvisionedTokenID");
                VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                txnDetail.setTokenUniqueReference(tokenData.getTokenLast4());
            }

            txnDetail.setTransactionIdentifier(jsTxnHistoryRec.getString("transactionID"));
            int transactionType = jsTxnHistoryRec.getInt("transactionType");
            if(transactionType == 0) {
                txnDetail.setTransactionType("Purchase");
            } else if(transactionType == 1) {
                txnDetail.setTransactionType("Refund");
            }
            txnDetail.setTransactionTimestamp(jsTxnHistoryRec.getString("transactionDate"));
            txnDetail.setMerchantName(jsTxnHistoryRec.getString("merchantName"));
            txnDetail.setAmount(jsTxnHistoryRec.getDouble("amount"));
            txnDetail.setCurrencyCode(jsTxnHistoryRec.getString("currencyCode"));

            switch (jsTxnHistoryRec.getInt("transactionStatus")) {
                case 1:
                    txnDetail.setAuthorizationStatus("Approved");
                    break;

                case 2:
                    txnDetail.setAuthorizationStatus("Refunded");
                    break;

                case 3:
                    txnDetail.setAuthorizationStatus("Declined");
                    break;

                case 4:
                    txnDetail.setAuthorizationStatus("Settled");
                    break;
            }
            txnDetail.setMerchantPostalCode(jsTxnHistoryRec.getString("merchantZipCode"));
            txnDetail.setAtc(jsTxnHistoryRec.getString("atc"));
            transactionDetails.add(txnDetail);
        }
        return transactionDetails;
    }

    /**
     * <p>Initiate registration with Transaction Details Services. This API is used for MasterCard.</p>
     *
     * @param tokenUniqueReference    The Token for which to register for transaction details
     * @param tdsRegistrationListener UI Listener
     */
    public static void registerWithTdsInitiate(final String tokenUniqueReference, final TdsRegistrationListener tdsRegistrationListener) {
        final ComvivaWalletListener walletListener = ComvivaFCMService.getWalletEventListener();
        final String displayableCardNo = McbpCardApi.getDisplayablePanDigits(tokenUniqueReference);

        final JSONObject jsGetRegCode = new JSONObject();
        try {
            final ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            jsGetRegCode.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            jsGetRegCode.put("tokenUniqueReference", tokenUniqueReference);
        } catch (JSONException e) {
            tdsRegistrationListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            tdsRegistrationListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
        }

        class GetRegCodeTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (tdsRegistrationListener != null) {
                    tdsRegistrationListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getRegCodeTdsUrl(), jsGetRegCode.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());

                        // Error while registration
                        if (respObj.has("errorCode")) {
                            tdsRegistrationListener.onError(SdkErrorImpl.getInstance(respObj.getInt("errorCode"), respObj.getString("errorDescription")));
                        } else {
                            tdsRegistrationListener.onSuccess();
                        }
                    } else {
                        tdsRegistrationListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                } catch (JSONException e) {
                    Log.d("Exception", e.getMessage());
                    walletListener.onTdsRegistrationError(displayableCardNo, "JSONError");
                    tdsRegistrationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                    walletListener.onTdsRegistrationError(displayableCardNo, "JSONError");
                    tdsRegistrationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        GetRegCodeTask getRegCodeTask = new GetRegCodeTask();
        getRegCodeTask.execute();
    }

    /**
     * TDS Notification data having second part of registration code 2 to complete TDS registration.
     *
     * @param tdsNotificationData Tds Notification Data
     */
    public static void registerWithTdsFinish(final TdsNotificationData tdsNotificationData) {
        final ComvivaWalletListener walletListener = ComvivaFCMService.getWalletEventListener();

        final ComvivaSdk comvivaSdk;
        try {
            comvivaSdk = ComvivaSdk.getInstance(null);
        } catch (SdkException e) {
            walletListener.onTdsRegistrationError(tdsNotificationData.getTokenUniqueReference(), e.getMessage());
            return;
        }

        if (!comvivaSdk.getPaymentAppInstanceId().equalsIgnoreCase(tdsNotificationData.getPaymentAppInstanceId())) {
            // PaymentAppInstanceId is not matching
            walletListener.onTdsRegistrationError(tdsNotificationData.getTokenUniqueReference(), "PaymentAppInstanceId is wrong");
            return;
        }

        final JSONObject jsGetRegCode = new JSONObject();
        try {
            jsGetRegCode.put("paymentAppInstanceId", tdsNotificationData.getPaymentAppInstanceId());
            jsGetRegCode.put("tokenUniqueReference", tdsNotificationData.getTokenUniqueReference());
        } catch (JSONException e) {
            walletListener.onTdsRegistrationError(tdsNotificationData.getTokenUniqueReference(), "JSON Error, wrong data from server");
            return;
        }

        class RegisterTdsTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getRegisterTdsUrl(), jsGetRegCode.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());

                        if (!respObj.has("errorCode")) {
                            SharedPreferences sharedPrefConf = comvivaSdk.getApplicationContext().getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPrefConf.edit();
                            editor.putBoolean(Constants.KEY_MDES_TDS_REG_STATUS, true);
                            editor.putString(Constants.KEY_TDS_REG_TOKEN_UNIQUE_REF, tdsNotificationData.getTokenUniqueReference());
                            editor.commit();
                            walletListener.onTdsRegistrationSuccess(McbpCardApi.getDisplayablePanDigits(tdsNotificationData.getTokenUniqueReference()));
                        }
                    }
                } catch (JSONException e) {
                    walletListener.onTdsRegistrationError(tdsNotificationData.getTokenUniqueReference(), "JSON Error");
                }
            }
        }
        RegisterTdsTask registerTdsTask = new RegisterTdsTask();
        registerTdsTask.execute();
    }

    /**
     * <p>This API is used by the Mobile Payment App to get recent transactions for one or more
     * Tokens. This API is used for MasterCard.</p>
     *
     * @param tokenUniqueReference       The Token for which to get transaction details.
     * @param transactionDetailsListener UI Listener
     */
    public static void getTransactionDetails(final String tokenUniqueReference, final TransactionDetailsListener transactionDetailsListener) {
        final JSONObject jsGetTxnDetails = new JSONObject();
        try {
            final ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            jsGetTxnDetails.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            jsGetTxnDetails.put("tokenUniqueReference", tokenUniqueReference);
        } catch (JSONException e) {
            transactionDetailsListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            transactionDetailsListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
        }

        class GetTxnDetailsTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (transactionDetailsListener != null) {
                    transactionDetailsListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getTransactionDetailsUrl(), jsGetTxnDetails.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());

                        // Error while registration
                        if (respObj.has("errorCode")) {
                            transactionDetailsListener.onError(SdkErrorImpl.getInstance(respObj.getInt("errorCode"), respObj.getString("errorDescription")));
                        } else {
                            // If Authentication received is different from previous one, then update.
                            /*String authenticationCode = respObj.getString("authenticationCode");
                            CommonDb commonDb = comvivaSdk.getCommonDb();
                            TdsRegistrationData tdsRegistrationData = commonDb.getTdsRegistrationData(tokenUniqueReference);
                            if(!tdsRegistrationData.getAuthenticationCode().equalsIgnoreCase(authenticationCode)) {
                                tdsRegistrationData.setAuthenticationCode(authenticationCode);
                                commonDb.saveTdsRegistrationCode(tdsRegistrationData);
                            }*/

                            // Fetch all transaction details
                            JSONArray arrTransactions = respObj.getJSONArray("transactions");
                            JSONObject tempTransactionDetail;
                            ArrayList<TransactionDetails> arrTxnDetails = new ArrayList<>();
                            TransactionDetails txnDetails;
                            String displayableCardNo;
                            for (int i = 0; i < arrTransactions.length(); i++) {
                                tempTransactionDetail = arrTransactions.getJSONObject(i);
                                txnDetails = new TransactionDetails();
                                displayableCardNo = McbpCardApi.getDisplayablePanDigits(tempTransactionDetail.getString("tokenUniqueReference"));
                                txnDetails.setTokenUniqueReference("XXXX XXXX XXXX " + displayableCardNo);
                                txnDetails.setRecordId(tempTransactionDetail.getString("recordId"));
                                if (tempTransactionDetail.has("transactionIdentifier")) {
                                    txnDetails.setTransactionIdentifier(tempTransactionDetail.getString("transactionIdentifier"));
                                }
                                txnDetails.setTransactionType(tempTransactionDetail.getString("transactionType"));
                                txnDetails.setAmount(tempTransactionDetail.getDouble("amount"));
                                txnDetails.setCurrencyCode(tempTransactionDetail.getString("currencyCode"));
                                txnDetails.setAuthorizationStatus(tempTransactionDetail.getString("authorizationStatus"));
                                txnDetails.setTransactionTimestamp(tempTransactionDetail.getString("transactionTimestamp"));
                                if (tempTransactionDetail.has("merchantName")) {
                                    txnDetails.setMerchantName(tempTransactionDetail.getString("merchantName"));
                                }
                                if (tempTransactionDetail.has("merchantType")) {
                                    txnDetails.setMerchantType(tempTransactionDetail.getString("merchantType"));
                                }
                                if (tempTransactionDetail.has("merchantPostalCode")) {
                                    txnDetails.setMerchantPostalCode(tempTransactionDetail.getString("merchantPostalCode"));
                                }
                                arrTxnDetails.add(txnDetails);
                            }
                            transactionDetailsListener.onSuccess(arrTxnDetails);
                        }
                    }
                } catch (JSONException e) {
                    transactionDetailsListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                }
            }
        }
        GetTxnDetailsTask getTxnDetailsTask = new GetTxnDetailsTask();
        getTxnDetailsTask.execute();
    }

    /**
     * <p>This API is used to unregister a specific Token from the Transaction Details Service, or
     * to opt out of the Transaction Details Service altogether. This API is used for MasterCard.
     * </p>
     *
     * @param tokenUniqueReference  The Token for which to unregister from transaction details.
     *                              If tokenUniqueReference is null, all Tokens for the Mobile Payment App instance will be unregistered.
     * @param unregisterTdsListener UI Listener
     */
    public static void unregisterWithTds(final String tokenUniqueReference, final UnregisterTdsListener unregisterTdsListener) {
        final JSONObject jsUnregisterTds = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            jsUnregisterTds.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            if (tokenUniqueReference != null) {
                jsUnregisterTds.put("tokenUniqueReference", tokenUniqueReference);
            }
        } catch (JSONException e) {
            unregisterTdsListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            unregisterTdsListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
        }

        class UnregisterTdsTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (unregisterTdsListener != null) {
                    unregisterTdsListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getUnregisterTdsUrl(), jsUnregisterTds.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());

                        // Error while registration
                        if (respObj.has("errorCode")) {
                            unregisterTdsListener.onError(SdkErrorImpl.getInstance(respObj.getInt("errorCode"), respObj.getString("errorDescription")));
                        } else {
                            unregisterTdsListener.onSuccess();
                        }
                    }
                } catch (JSONException e) {
                    unregisterTdsListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                }
            }
        }
        UnregisterTdsTask unregisterTdsTask = new UnregisterTdsTask();
        unregisterTdsTask.execute();
    }

    /**
     * This API is used to get the transaction History .
     *
     * @param paymentCard                Payment Card whose transaction history needs to be fetched.
     * @param count                      Number of records to retrieve. Maximum is 10. If not specified, the maximum number of records will be returned, up to 10, inclusive.
     * @param transactionHistoryListener UI Listener
     */
    public static void getTransactionHistory(final PaymentCard paymentCard, final int count, final TransactionHistoryListener transactionHistoryListener) {
        final JSONObject jsTransactionHistoryObject = new JSONObject();
        try {
            jsTransactionHistoryObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), paymentCard.getCardUniqueId());
            if (count < 0 || count > 10) {
                if (transactionHistoryListener != null) {
                    transactionHistoryListener.onError(SdkErrorStandardImpl.SDK_INVALID_NO_OF_TXN_RECORDS);
                }
            } else {
                jsTransactionHistoryObject.put("Count", count);
            }
        } catch (JSONException e) {
            if (transactionHistoryListener != null) {
                transactionHistoryListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            }
            return;
        }

        class GetTransactionHistoryTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (transactionHistoryListener != null) {
                    transactionHistoryListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSTransactionHistory(), jsTransactionHistoryObject.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject jsTransactionHistoryResponse = new JSONObject(httpResponse.getResponse());
                        if (jsTransactionHistoryResponse.has(Tags.RESPONSE_CODE.getTag())) {
                            int respCode = jsTransactionHistoryResponse.getInt(Tags.RESPONSE_CODE.getTag());
                            // Error
                            if (respCode != 200) {
                                if (transactionHistoryListener != null) {
                                    transactionHistoryListener.onError(SdkErrorImpl.getInstance(respCode, jsTransactionHistoryResponse.getString(Tags.MESSAGE.getTag())));
                                }
                                return;
                            } else {
                                ArrayList<TransactionDetails> encryptedTransactionInfo = parseTransactionHistoryData(jsTransactionHistoryResponse);
                                if (transactionHistoryListener != null) {
                                    transactionHistoryListener.onSuccess(encryptedTransactionInfo);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        if (transactionHistoryListener != null) {
                            transactionHistoryListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                        }
                    }
                } else {
                    if (transactionHistoryListener != null) {
                        transactionHistoryListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                }
            }
        }
        GetTransactionHistoryTask getTransactionHistoryTask = new GetTransactionHistoryTask();
        getTransactionHistoryTask.execute();
    }

}
