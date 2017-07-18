package com.comviva.hceservice.mdes.tds;


import android.os.AsyncTask;

import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.common.database.CommonDb;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionHistory {
    public static void registerWithTdsInitiate(final String tokenUniqueReference, final TdsRegistrationListener tdsRegistrationListener) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);

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
                            comvivaHce.getCommonDb().saveTdsRegistrationCode(tdsRegistrationData);
                            tdsRegistrationListener.onSuccess();
                        }
                    }
                } catch (JSONException e) {
                    tdsRegistrationListener.onError("JSON Error");
                }
            }
        }
        GetRegCodeTask getRegCodeTask = new GetRegCodeTask();
        getRegCodeTask.execute();
    }

    public static void registerWithTdsFinish(final TdsNotificationData tdsNotificationData) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        if (!comvivaHce.getPaymentAppInstanceId().equalsIgnoreCase(tdsNotificationData.getPaymentAppInstanceId())) {
            // PaymentAppInstanceId is not matching
            return;
        }

        final JSONObject jsGetRegCode = new JSONObject();
        try {
            jsGetRegCode.put("paymentAppInstanceId", tdsNotificationData.getPaymentAppInstanceId());
            jsGetRegCode.put("tokenUniqueReference", tdsNotificationData.getTokenUniqueReference());
        } catch (JSONException e) {
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
                            comvivaHce.getCommonDb().saveTdsRegistrationCode(tdsRegistrationData);
                        }
                    }
                } catch (JSONException e) {
                }
            }
        }
        RegisterTdsTask registerTdsTask = new RegisterTdsTask();
        registerTdsTask.execute();
    }

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
                            String authenticationCode = respObj.getString("authenticationCode");
                            CommonDb commonDb = comvivaHce.getCommonDb();
                            TdsRegistrationData tdsRegistrationData = commonDb.getTdsRegistrationData(tokenUniqueReference);
                            if(!tdsRegistrationData.getAuthenticationCode().equalsIgnoreCase(authenticationCode)) {
                                tdsRegistrationData.setAuthenticationCode(authenticationCode);
                                commonDb.saveTdsRegistrationCode(tdsRegistrationData);
                            }

                            // Fetch all transaction details
                            JSONArray arrTransactions = respObj.getJSONArray("transactions");
                            TransactionDetails[] transactionDetails = new TransactionDetails[arrTransactions.length()];
                            JSONObject tempTransactionDetail;
                            for(int i = 0; i < arrTransactions.length(); i++) {
                                tempTransactionDetail = arrTransactions.getJSONObject(i);
                                transactionDetails[i] = new TransactionDetails();
                                transactionDetails[i].setTokenUniqueReference(tempTransactionDetail.getString("tokenUniqueReference"));
                                transactionDetails[i].setRecordId(tempTransactionDetail.getString("recordId"));
                                if(tempTransactionDetail.has("transactionIdentifier")) {
                                    transactionDetails[i].setTransactionIdentifier(tempTransactionDetail.getString("transactionIdentifier"));
                                }
                                transactionDetails[i].setTransactionType(tempTransactionDetail.getString("transactionType"));
                                transactionDetails[i].setAmount(tempTransactionDetail.getDouble("amount"));
                                transactionDetails[i].setCurrencyCode(tempTransactionDetail.getString("currencyCode"));
                                transactionDetails[i].setAuthorizationStatus(tempTransactionDetail.getString("authorizationStatus"));
                                transactionDetails[i].setTransactionTimestamp(tempTransactionDetail.getString("transactionTimestamp"));
                                if(tempTransactionDetail.has("merchantName")) {
                                    transactionDetails[i].setMerchantName(tempTransactionDetail.getString("merchantName"));
                                }
                                if(tempTransactionDetail.has("merchantType")) {
                                    transactionDetails[i].setMerchantType(tempTransactionDetail.getString("merchantType"));
                                }
                                if(tempTransactionDetail.has("merchantPostalCode")) {
                                    transactionDetails[i].setMerchantPostalCode(tempTransactionDetail.getString("merchantPostalCode"));
                                }
                            }
                            transactionDetailsListener.onSuccess(transactionDetails);
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

}
