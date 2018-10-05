package com.comviva.hceservice.fcm;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import com.comviva.hceservice.apiCalls.NetworkApi;
import com.comviva.hceservice.common.CardState;
import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.listeners.GetCardMetaDataListener;
import com.comviva.hceservice.listeners.TokenDataUpdateListener;
import com.comviva.hceservice.pojo.transactionhistorymdes.TransactionHistoryRegisterMdesResponse;
import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.util.Constants;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercard.mpsdk.componentinterface.RolloverInProgressException;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.TokenStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.comviva.hceservice.util.Constants.DELETE_USER;
import static com.comviva.hceservice.util.Constants.SUSUPEND_USER;
import static com.comviva.hceservice.util.Constants.TOKEN_STATUS_UPDATED;
import static com.comviva.hceservice.util.Constants.UNSUSPEND_USER;
import static com.comviva.hceservice.util.Constants.UPDATE_TXN_HISTORY;
import static com.visa.cbp.sdk.facade.util.ContextHelper.getApplicationContext;

/**
 * Service Class implementing Firebase Messaging Service.
 */
public class ComvivaFCMService {

    private Digitization digitization;
    private static ComvivaFCMService comvivaFCMService;
    private static Application applicationContext;
    private SDKData sdkData;
    private String tokenRef;
    private NetworkApi networkApi;


    private ComvivaFCMService(Application application) {

        try {
            sdkData = SDKData.getInstance();
            if (null == sdkData.getComvivaSdk()) {
                sdkData.setComvivaSdk(ComvivaSdk.getInstance(application));
            }
            networkApi = new NetworkApi();
        } catch (Exception e) {
            Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
        }
    }


    /**
     * Returns Singleton Instance of this class.
     *
     * @return ComvivaFCMService instance
     */
    public static ComvivaFCMService getInstance(Application application) {

        if (comvivaFCMService == null) {
            applicationContext = application;
            comvivaFCMService = new ComvivaFCMService(application);
        }
        return comvivaFCMService;
    }


    private ServerResponseListener serverResponseListener = new ServerResponseListener() {
        @Override
        public void onRequestCompleted(Object result, Object listener) {

            if (result != null && result instanceof TransactionHistoryRegisterMdesResponse) {
                TransactionHistoryRegisterMdesResponse transactionHistoryRegisterMdesResponse = (TransactionHistoryRegisterMdesResponse) result;
                if (Constants.HTTP_RESPONSE_CODE_200.equals(transactionHistoryRegisterMdesResponse.getResponseCode()) && null != transactionHistoryRegisterMdesResponse.getRegistrationStatus()) {
                    CommonUtil.setSharedPreference(tokenRef, transactionHistoryRegisterMdesResponse.getRegistrationStatus(), Constants.SHARED_PREF_MDES_CARD_STATUS_DETAILS);
                }
            }
        }


        @Override
        public void onRequestError(String message, Object listener) {

        }
    };


    /**
     * Invoke this API when FCM notification comes.
     *
     * @param remoteMessage RemoteMessage object received through FCM notification
     */
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map data;
        String strNotificationData;
        // Grab the dataReceived
        data = remoteMessage.getData();
        try {
            if (data.containsKey("TYPE")) {
                String type = data.get("TYPE").toString();
                if (type.equalsIgnoreCase("MDES")) {
                    if (data.containsKey(Constants.MESSAGE_TAG)) {
                        JSONObject jsonObject = new JSONObject(data);
                        strNotificationData = jsonObject.getString(Constants.MESSAGE_TAG);
                        strNotificationData = new String(Base64.decode(strNotificationData, Base64.DEFAULT));
                        SDKData sdkData = SDKData.getInstance();
                        Log.d(Tags.DEBUG_LOG.getTag(), strNotificationData);
                        sdkData.getMcbp().getRemoteCommunicationManager().processNotificationData(strNotificationData);
                        Log.d(Tags.DEBUG_LOG.getTag(), "sent for processing");
                    } else if (data.containsKey(Tags.SUBTYPE.getTag()) && null != data.get(Tags.SUBTYPE.getTag()) && data.get(Tags.SUBTYPE.getTag()).equals(Tags.MDES_LCM.getTag())) {
                        JSONArray jsonArray = new JSONArray(data.get(Tags.TOKENS.getTag()).toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject cardDetails = jsonArray.getJSONObject(i);
                            if (cardDetails.has(Tags.TOKEN_UNIQUE_REFERENCE.getTag())) {
                                if (cardDetails.has(Tags.STATUS.getTag())) {
                                    switch (CommonUtil.getCardStatusFromString(cardDetails.get(Tags.STATUS.getTag()).toString())) {
                                        case ACTIVE:
                                            try {
                                                sdkData.getMcbp().getCardManager().activateCard(sdkData.getMcbp().getCardManager().getCardById(cardDetails.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString()));
                                                sdkData.getMcbp().getCardManager().getCardById(cardDetails.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString()).replenishCredentials();
                                                ArrayList<PaymentCard> cardList = null;
                                                try {
                                                    cardList = sdkData.getComvivaSdk().getAllCards();
                                                    if ((cardList != null) && (cardList.size() == 1) && (cardList.get(0).getCardState().equals(CardState.ACTIVE))) {
                                                        sdkData.getComvivaSdk().setDefaultCard(cardList.get(0));
                                                    }
                                                } catch (Exception e) {
                                                    Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
                                                }
                                                networkApi.getRegisterTransactionHistoryMdes(cardDetails.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString());
                                                networkApi.setServerAuthenticateListener(serverResponseListener);
                                                tokenRef = cardDetails.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString();
                                            } catch (RolloverInProgressException e1) {
                                                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e1));
                                            } catch (Exception e2) {
                                                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e2));
                                            }
                                            break;
                                        case SUSPENDED:
                                            try {
                                                sdkData.getMcbp().getCardManager().suspendCard(sdkData.getMcbp().getCardManager().getCardById(cardDetails.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString()));
                                            } catch (RolloverInProgressException e1) {
                                                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e1));
                                            } catch (Exception e2) {
                                                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e2));
                                            }
                                            break;
                                        case MARKED_FOR_DELETION:
                                            try {
                                                sdkData.getMcbp().getCardManager().deleteCard(sdkData.getMcbp().getCardManager().getCardById(cardDetails.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString()));
                                            } catch (RolloverInProgressException e1) {
                                                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e1));
                                            } catch (Exception e2) {
                                                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e2));
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    } else if ((data.containsKey(Tags.SUBTYPE.getTag()) && null != data.get(Tags.SUBTYPE.getTag()) && data.get(Tags.SUBTYPE.getTag()).equals(Tags.MDES_TXN.getTag()))) {
                        CommonUtil.setSharedPreference(data.get(Tags.TOKEN_UNIQUE_REFERENCE.getTag()).toString(), data.get(Tags.REGISTRATION_STATUS.getTag()).toString(), Constants.SHARED_PREF_MDES_CARD_STATUS_DETAILS);
                    }
                } else if (type.equalsIgnoreCase("Vts")) { // Remote Notifications for Vts
                    digitization = Digitization.getInstance();
                    ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(applicationContext);
                    if (data.containsKey(Constants.OPERATION) && data.get(Constants.OPERATION).toString().equalsIgnoreCase(TOKEN_STATUS_UPDATED)) {
                        final String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        final VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        digitization.getTokenStatus(paymentCard, new TokenDataUpdateListener() {
                            @Override
                            public void onSuccess(String newString) {

                                CommonUtil.broadcastMessage(getApplicationContext(), TOKEN_STATUS_UPDATED, paymentCard.getCardUniqueId(), newString);
                            }


                            @Override
                            public void onError(SdkError sdkError) {

                            }


                            @Override
                            public void onStarted() {

                            }
                        });
                        // call get Token status
                    } else if (data.containsKey(Constants.OPERATION) && data.get(Constants.OPERATION).toString().equalsIgnoreCase(Constants.KEY_STATUS_UPDATED)) {
                        String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        digitization.replenishTransactionCredential(paymentCard, new ResponseListener() {
                            @Override
                            public void onStarted() {

                            }


                            @Override
                            public void onSuccess() {

                                Log.d("Vts NOtification", "replenishTransactionCredential Successful");
                            }


                            @Override
                            public void onError(SdkError sdkError) {

                            }
                        });
                        //Call replenish, conform replenish and replenish ODA data
                    } else if (data.containsKey(Constants.OPERATION) && data.get(Constants.OPERATION).toString().equalsIgnoreCase(Constants.UPDATE_CARD_META_DATA)) {
                        String vPanEnrollmentID = data.get("vPanEnrollmentId").toString();
                       /* VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vPanEnrollmentID);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        final PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);*/
                        digitization.getCardMetaData(vPanEnrollmentID, new GetCardMetaDataListener() {
                            @Override
                            public void onStarted() {

                            }


                            @Override
                            public void onSuccess(CardMetaData cardMetaData) {

                                Log.d("Vts NOtification", "replenishTransactionCredential Successful");
                                // publish("Meta Data Updated " , "Success" );
                            }


                            @Override
                            public void onError(SdkError sdkError) {

                            }
                        });
                        //UPDATE_CARD_METADATA
                    } else if (data.containsKey(Constants.OPERATION) && data.get(Constants.OPERATION).toString().equalsIgnoreCase(UPDATE_TXN_HISTORY)) {
                        String vProvisionedTokenId = data.get("vprovisionedTokenId").toString();
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(vProvisionedTokenId);
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        PaymentCard paymentCard = PaymentCard.getPaymentCard(tokenData);
                        CommonUtil.broadcastMessage(getApplicationContext(), UPDATE_TXN_HISTORY, paymentCard.getCardUniqueId(), null);
                     /*   TransactionHistoryData.getTransactionHistory(paymentCard, 4, new TransactionHistoryListener() {
                            @Override
                            public void onSuccess(ArrayList transactionInfo) {

                            }

                            @Override
                            public void onStarted() {
                            }

                            @Override
                            public void onError(SdkError sdkError) {
                            }
                        });*/
                        //Call getTransctionHistory and update txnHistory
                    }
                } else if (type.equalsIgnoreCase("ALL")) {
                    ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(applicationContext);
                    VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                    ArrayList<PaymentCard> paymentCards = ComvivaSdk.getInstance(applicationContext).getAllCards();
                    for (int i = 0; i < paymentCards.size(); i++) {
                        if (paymentCards.get(i).getCardType().toString().equalsIgnoreCase(CardType.VTS.toString())) {
                            if (data.containsKey(Constants.OPERATION)) {
                                if (data.get(Constants.OPERATION).toString().equalsIgnoreCase(SUSUPEND_USER)) {
                                    visaPaymentSDK.updateTokenStatus(((TokenData) paymentCards.get(i).getCurrentCard()).getTokenKey(), TokenStatus.SUSPENDED);
                                } else if (data.get(Constants.OPERATION).toString().equalsIgnoreCase(UNSUSPEND_USER)) {
                                    visaPaymentSDK.updateTokenStatus(((TokenData) paymentCards.get(i).getCurrentCard()).getTokenKey(), TokenStatus.RESUME);
                                }
                            }
                        } else if ((paymentCards.get(i).getCardType().toString().equalsIgnoreCase(CardType.MDES.toString()))) {
                        }
                    }
                    if (data.containsKey(Constants.OPERATION)) {
                        CommonUtil.broadcastMessage(getApplicationContext(), data.get(Constants.OPERATION).toString(), null, null);
                    }
                    if (data.get(Constants.OPERATION).toString().equalsIgnoreCase(DELETE_USER)) {
                        comvivaSdk.resetDevice();
                        CommonUtil.broadcastMessage(getApplicationContext(), DELETE_USER, null, null);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(Tags.DEBUG_LOG.getTag(), String.valueOf(e));
            return;
        }
    }
}