package com.comviva.hceservice.mdes.tds;


import android.os.AsyncTask;

import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.common.ComvivaWalletListener;
import com.comviva.hceservice.fcm.ComvivaFCMService;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mcbp.api.McbpCardApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This Class contains all transaction history related APIs.
 */
public class TransactionHistory {
    /**
     * Initiate registration with Transaction Details Services.
     * @param tokenUniqueReference      The Token for which to register for transaction details
     * @param tdsRegistrationListener   UI Listener
     */
    public static void registerWithTdsInitiate(final String tokenUniqueReference, final TdsRegistrationListener tdsRegistrationListener) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        final ComvivaWalletListener walletListener = ComvivaFCMService.getWalletEventListener();
        final String displayableCardNo = McbpCardApi.getDisplayablePanDigits(tokenUniqueReference);

        final JSONObject jsGetRegCode = new JSONObject();
        try {
            jsGetRegCode.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            jsGetRegCode.put("tokenUniqueReference", tokenUniqueReference);
        } catch (JSONException e) {
            tdsRegistrationListener.onError("JSON Error");
        }

        class GetRegCodeTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (tdsRegistrationListener != null) {
                    tdsRegistrationListener.onRegistrationStarted();
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
                            tdsRegistrationListener.onError(respObj.getString("errorDescription"));
                        } else {
                            // Save registration code for further completion of the TDS registration
                            String registrationCode1 = respObj.getString("registrationCode1");

                            TdsRegistrationData tdsRegistrationData = new TdsRegistrationData();
                            tdsRegistrationData.setTokenUniqueReference(tokenUniqueReference);
                            tdsRegistrationData.setTdsRegistrationCode1(registrationCode1);
                            //comvivaHce.getCommonDb().saveTdsRegistrationCode(tdsRegistrationData);
                            tdsRegistrationListener.onSuccess();
                        }
                    } else {
                        tdsRegistrationListener.onError(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    walletListener.onTdsRegistrationError(displayableCardNo, "JSONError");
                    tdsRegistrationListener.onError("JSON Error");
                } catch (Exception e) {
                    e.printStackTrace();
                    walletListener.onTdsRegistrationError(displayableCardNo, "JSONError");
                    tdsRegistrationListener.onError("JSON Error");
                }
            }
        }
        GetRegCodeTask getRegCodeTask = new GetRegCodeTask();
        getRegCodeTask.execute();
    }

    /**
     * @param tdsNotificationData
     */
    public static void registerWithTdsFinish(final TdsNotificationData tdsNotificationData) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        final ComvivaWalletListener walletListener = ComvivaFCMService.getWalletEventListener();
        if (!comvivaHce.getPaymentAppInstanceId().equalsIgnoreCase(tdsNotificationData.getPaymentAppInstanceId())) {
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
                            TdsRegistrationData tdsRegistrationData = new TdsRegistrationData();
                            tdsRegistrationData.setTokenUniqueReference(tdsNotificationData.getTokenUniqueReference());
                            tdsRegistrationData.setAuthenticationCode(respObj.getString("authenticationCode"));
                            tdsRegistrationData.setTdsUrl(respObj.getString("tdsUrl"));
                            //comvivaHce.getCommonDb().saveTdsRegistrationCode(tdsRegistrationData);

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
     * This API is used by the Mobile Payment App to get recent transactions for one or more Tokens.
     * @param tokenUniqueReference          The Token for which to get transaction details.
     * @param transactionDetailsListener    UI Listener
     */
    public static void getTransactionDetails(final String tokenUniqueReference, final TransactionDetailsListener transactionDetailsListener) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);

        final JSONObject jsGetTxnDetails = new JSONObject();
        try {
            jsGetTxnDetails.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            jsGetTxnDetails.put("tokenUniqueReference", tokenUniqueReference);
        } catch (JSONException e) {
            transactionDetailsListener.onError("JSON Error");
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
                            transactionDetailsListener.onError(respObj.getString("errorDescription"));
                        } else {
                            // If Authentication received is different from previous one, then update.
                            /*String authenticationCode = respObj.getString("authenticationCode");
                            CommonDb commonDb = comvivaHce.getCommonDb();
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
                    transactionDetailsListener.onError("JSON Error");
                }
            }
        }
        GetTxnDetailsTask getTxnDetailsTask = new GetTxnDetailsTask();
        getTxnDetailsTask.execute();
    }

    /**
     * This API is used to unregister a specific Token from the Transaction Details Service, or to opt out of the Transaction Details Service altogether.
     * @param tokenUniqueReference  The Token for which to unregister from transaction details.
     *                              If tokenUniqueReference is null, all Tokens for the Mobile Payment App instance will be unregistered.
     * @param unregisterTdsListener UI Listener
     */
    public static void unregisterWithTds(final String tokenUniqueReference, final UnregisterTdsListener unregisterTdsListener) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);

        final JSONObject jsUnregisterTds = new JSONObject();
        try {
            jsUnregisterTds.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            if (tokenUniqueReference != null) {
                jsUnregisterTds.put("tokenUniqueReference", tokenUniqueReference);
            }
        } catch (JSONException e) {
            unregisterTdsListener.onError("JSON Error");
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
                            unregisterTdsListener.onError(respObj.getString("errorDescription"));
                        } else {
                            unregisterTdsListener.onSuccess();
                        }
                    }
                } catch (JSONException e) {
                    unregisterTdsListener.onError("JSON Error");
                }
            }
        }
        UnregisterTdsTask unregisterTdsTask = new UnregisterTdsTask();
        unregisterTdsTask.execute();
    }

}
