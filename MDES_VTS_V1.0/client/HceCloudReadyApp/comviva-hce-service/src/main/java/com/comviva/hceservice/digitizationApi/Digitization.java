package com.comviva.hceservice.digitizationApi;

import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.digitizationApi.asset.AssetType;
import com.comviva.hceservice.digitizationApi.asset.GetAssetResponse;
import com.comviva.hceservice.digitizationApi.asset.MediaContent;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.LuhnUtil;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.MdesMcbpWalletApi;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.remotemanagement.mdes.RemoteManagementHandler;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.exception.VisaPaymentSDKException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Contains all Digitization APIs.
 */
public class Digitization {
    private static Digitization instance;
    private DigitizationMdes digitizationMdes;
    private DigitizationVts digitizationVts;

    private Digitization() {
    }

    private void resetDigitization() {
        digitizationMdes = null;
        digitizationVts = null;
    }

    /**
     * Returns singleton instance of This class.
     *
     * @return Instance of this class
     */
    public static Digitization getInstance() {
        if (instance == null) {
            instance = new Digitization();
        }
        return instance;
    }

    /**
     * Checks that Card is eligible for digitization or not.
     *
     * @param cardEligibilityRequest   Eligibility request
     * @param checkEligibilityListener Eligibility Response
     */
    public void checkCardEligibility(CardEligibilityRequest cardEligibilityRequest, final CheckCardEligibilityListener checkEligibilityListener) {
        resetDigitization();
        String cardNumber = cardEligibilityRequest.getAccountNumber();

        // Validate Card number
        if (cardNumber.length() < 13 || cardNumber.length() > 19 || !LuhnUtil.checkLuhn(cardNumber)) {
            checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_INVALID_CARD_NUMBER);
            return;
        }

        CardType cardType = CardType.checkCardType(cardNumber);
        switch (cardType) {
            case MDES:
                digitizationMdes = new DigitizationMdes();
                digitizationMdes.checkCardEligibilityMdes(cardEligibilityRequest, checkEligibilityListener);
                break;

            case VTS:
                digitizationVts = new DigitizationVts();
                digitizationVts.enrollPanVts(cardEligibilityRequest, checkEligibilityListener);
                break;

            default:
                checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_UNSUPPORTED_SCHEME);
        }
    }

    /**
     * Fetches Asset's value from payment App Server.
     *
     * @param assetId Asset ID
     * @return GetAssetResponse object
     */
    public GetAssetResponse getAsset(String assetId) {
        final Map<String, String> queryMap = new HashMap<>();
        queryMap.put("assetId", assetId);
        final GetAssetResponse getAssetResponse = new GetAssetResponse();

        class GetAssetTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.getRequest(UrlUtil.getAssetUrl(), queryMap);
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        getAssetResponse.setResponseCode(200);
                        getAssetResponse.setResponseMessage("Successful");

                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        JSONArray arrMediaContents = respObj.getJSONArray("mediaContents");
                        int noOfAssets = arrMediaContents.length();
                        MediaContent[] mediaContents = new MediaContent[noOfAssets];
                        JSONObject jsMediaContent;
                        for (int i = 0; i < arrMediaContents.length(); i++) {
                            mediaContents[i] = new MediaContent();
                            jsMediaContent = (JSONObject) arrMediaContents.get(i);
                            String type = jsMediaContent.getString("type");
                            AssetType assetType = AssetType.valueOf(type);
                            mediaContents[i].setAssetType(assetType);
                            mediaContents[i].setData(jsMediaContent.getString("data"));
                            switch (assetType) {
                                // Height and Width is only application for image only
                                case IMAGE_PNG:
                                    mediaContents[i].setHeight(Integer.parseInt(jsMediaContent.getString("height")));
                                    mediaContents[i].setWidth(Integer.parseInt(jsMediaContent.getString("width")));
                                    break;
                            }
                        }
                        getAssetResponse.setMediaContents(mediaContents);
                    } else {
                        getAssetResponse.setResponseCode(httpResponse.getStatusCode());
                        getAssetResponse.setResponseMessage(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                    getAssetResponse.setResponseCode(httpResponse.getStatusCode());
                    getAssetResponse.setResponseMessage("Wrong data from server");
                }
            }
        }
        GetAssetTask getAssetTask = new GetAssetTask();
        try {
            getAssetTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("ComvivaSdkException", e.getMessage());
        }
        return getAssetResponse;
    }

    /**
     * Digitize the card.
     *
     * @param digitizationRequest  Digitization Request
     * @param digitizationListener UI Listener
     */
    public void digitize(DigitizationRequest digitizationRequest, final DigitizationListener digitizationListener) {
        try {
            // Check Card Eligibility is not invoked earlier
            if (digitizationMdes == null && digitizationVts == null) {
                digitizationListener.onError(SdkErrorStandardImpl.SDK_CARD_ELIGIBILITY_NOT_PERFORMED);
                return;
            }

            switch (digitizationRequest.getCardType()) {
                case MDES:
                    if (digitizationMdes != null && digitizationMdes.getCardEligibilityResponse() == null) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_CARD_ELIGIBILITY_NOT_PERFORMED);
                        return;
                    }
                    digitizationMdes.digitize(digitizationRequest, digitizationListener);
                    break;

                case VTS:
                    if (digitizationVts != null && digitizationVts.getEnrollPanResponse() == null) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_CARD_ELIGIBILITY_NOT_PERFORMED);
                        return;
                    }
                    digitizationVts.provisionToken(digitizationRequest, digitizationListener);
                    break;

                case UNKNOWN:
                    digitizationListener.onError(SdkErrorStandardImpl.SDK_UNSUPPORTED_SCHEME);
            }
        } finally {
            resetDigitization();
        }
    }

    /**
     * Request new session to complete pending task.
     */
    public void requestSession() {
        final JSONObject requestSessionReq = new JSONObject();
        try {
            byte[] baMobKeySetId = RemoteManagementHandler.getInstance().getLdeRemoteManagementService().getMobileKeySetIdAsByteArray().getBytes();
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance();
            requestSessionReq.put("paymentAppProviderId", /*comvivaSdk.getPaymentAppProviderId()*/"ComvivaWallet");
            requestSessionReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            requestSessionReq.put("mobileKeysetId", new String(baMobKeySetId));
        } catch (JSONException e) {
            Log.d("ComvivaSdkError", e.getMessage());
        }

        class RequestSessionTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getRequestSessionUrl(), requestSessionReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    Log.d("Seeesion Info", "Session Started");
                } else {
                    Log.d("Seeesion Info", "Session Failed");
                }
            }
        }
        RequestSessionTask requestSessionTask = new RequestSessionTask();
        requestSessionTask.execute();
    }

    /**
     * This API is used to Suspend, UnSuspend and Delete Token. <br>
     * At a time, you can provide one type of card only. <br>
     * Note - In case of MasterCard, you can provide more than one card. <br>
     * In case of Visa, you can provide only one card<br>
     *
     * @param cardLcmRequest  Card Life Cycle Management Request
     * @param cardLcmListener UI Listener
     */
    public void performCardLcm(final CardLcmRequest cardLcmRequest, final CardLcmListener cardLcmListener) {
        ArrayList<PaymentCard> cards = cardLcmRequest.getPaymentCards();
        ArrayList<PaymentCard> mdesCardList = new ArrayList<>();
        ArrayList<PaymentCard> vtsCardList = new ArrayList<>();

        for (PaymentCard card : cards) {
            switch (card.getCardType()) {
                case MDES:
                    mdesCardList.add(card);
                    break;

                case VTS:
                    vtsCardList.add(card);

                default:
                    cardLcmListener.onError(SdkErrorStandardImpl.SDK_UNSUPPORTED_SCHEME);
                    return;
            }
        }

        int noOfVtsCards = vtsCardList.size();
        int noOfMdesCards = mdesCardList.size();

        // In one method call, only one type of cards is supported.
        if (noOfVtsCards > 0 && noOfMdesCards > 0) {
            cardLcmListener.onError(SdkErrorStandardImpl.SDK_MORE_TYPE_OF_CARD_IN_LCM);
            return;
        }

        // If CardList contains only Visa card then only one card is allowed.
        if (noOfVtsCards != 0 && noOfMdesCards > 1) {
            cardLcmListener.onError(SdkErrorStandardImpl.SDK_ONLY_ONE_VISA_CARD_IN_LCM);
            return;
        }

        // If list contains only MasterCard
        if (noOfMdesCards > 0) {
            if (digitizationMdes == null) {
                digitizationMdes = new DigitizationMdes();
            }
            digitizationMdes.performCardLcm(mdesCardList, cardLcmRequest.getCardLcmOperation(), cardLcmRequest.getReasonCode(), cardLcmListener);
        } else {
            // List contains only one visa Card
            if (digitizationVts == null) {
                digitizationVts = new DigitizationVts();
            }
            digitizationVts.performCardLcm(vtsCardList.get(0), cardLcmRequest.getCardLcmOperation(), cardLcmRequest.getReasonCode(), cardLcmListener);
        }
    }

    /**
     * This API is used to request an Activation Code be sent to authenticate the Cardholder.
     *
     * @param tokenUniqueReference   The Token for which to send an Activation Code.
     * @param authenticationMethod   Identifies the AuthenticationMethod chosen by the Cardholder from the list of AuthenticationMethods
     * @param activationCodeListener UI Listener
     */
    public void requestActivationCode(final String tokenUniqueReference,
                                      final AuthenticationMethod authenticationMethod,
                                      final RequestActivationCodeListener activationCodeListener) {
        final JSONObject jsReqActCodeReq = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance();
            jsReqActCodeReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            jsReqActCodeReq.put("tokenUniqueReference", tokenUniqueReference);

            JSONObject jsAuthenticationMethod = new JSONObject();
            jsAuthenticationMethod.put("id", authenticationMethod.getId());
            jsAuthenticationMethod.put("type", authenticationMethod.getType().name());
            jsAuthenticationMethod.put("value", authenticationMethod.getValue());
            jsReqActCodeReq.put("authenticationMethod", jsAuthenticationMethod);
        } catch (JSONException e) {
            activationCodeListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        }

        class ReqActivationCodeTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activationCodeListener.onStarted();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getRequestActivationCodeUrl(), jsReqActCodeReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    // Activation Code sent successfully
                    if (httpResponse.getStatusCode() == 200) {
                        activationCodeListener.onSuccess(httpResponse.getResponse());
                    } else {
                        activationCodeListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                } catch (Exception e) {
                    activationCodeListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        ReqActivationCodeTask reqActivationCodeTask = new ReqActivationCodeTask();
        reqActivationCodeTask.execute();
    }

    /**
     * This API is used to activate a Token for first-time use if the digitization decision was to "Require Additional Authentication" in the Digitize response
     *
     * @param tokenUniqueReference The Token to be activated.
     * @param activationCode       Activation Code received by Cardholder
     * @param type                 Type of Activation Code
     * @param activateListener     UI listener
     */
    public void activate(final String tokenUniqueReference, final String activationCode, ActivationCodeType type,
                         final ActivateListener activateListener) {
        final JSONObject jsActivateReq = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance();
            jsActivateReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            jsActivateReq.put("tokenUniqueReference", tokenUniqueReference);
            switch (type) {
                case AUTHENTICATION_CODE:
                    jsActivateReq.put("authenticationCode", activationCode);
                    break;

                case TOKENIZATION_AUTHENTICATION_VALUE:
                    jsActivateReq.put("tokenizationAuthenticationValue", activationCode);
                    break;
            }
        } catch (JSONException e) {
            activateListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }

        class ActivateTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activateListener.onStarted();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getActivateUrl(), jsActivateReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsResponse = new JSONObject(httpResponse.getResponse());
                        ActivationResult result = ActivationResult.valueOf(jsResponse.getString("result"));
                        switch (result) {
                            case EXPIRED_CODE:
                                activateListener.onExpiredCode();
                                break;

                            case EXPIRED_SESSION:
                                activateListener.onSessionExpired();
                                break;

                            case INCORRECT_CODE:
                                activateListener.onIncorrectCode();
                                break;

                            case INCORRECT_CODE_RETRIES_EXCEEDED:
                                activateListener.onRetriesExceeded();
                                break;

                            case INCORRECT_TAV:
                                activateListener.onIncorrectTAV();
                                break;

                            case SUCCESS:
                                activateListener.onSuccess();
                                break;
                        }
                    } else {
                        activateListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                } catch (JSONException e) {
                    activateListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                }
            }
        }
        ActivateTask activateTask = new ActivateTask();
        activateTask.execute();
    }

    public void changePin(final String tokenUniqueReference,
                          final String oldPin,
                          final String newPin) {
        try {
            if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
                MdesMcbpWalletApi.changeWalletPin(oldPin.getBytes(), newPin.getBytes());
            } else {
                McbpCardApi.changePin(tokenUniqueReference, oldPin.getBytes(), newPin.getBytes());
            }
        } catch (AlreadyInProcessException e) {
            e.printStackTrace();
        } finally {

            RemoteManagementHandler.getInstance().clearPendingAction();
        }
    }

    /**
     * Set Mobiile PIN if it is not already set.
     *
     * @param tokenUniqueReference TokenUniqueReference of which PIN is to set. If not provided wallet level PIN will be set.
     * @param newPin               New Mobile PIN
     */
    public void setPin(final String tokenUniqueReference, final String newPin) {
        try {
            if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
                MdesMcbpWalletApi.setWalletPin(newPin.getBytes());
            } else {
                McbpCardApi.setPin(tokenUniqueReference, newPin.getBytes());
            }
        } catch (AlreadyInProcessException e) {
            e.printStackTrace();
        } finally {
            RemoteManagementHandler.getInstance().clearPendingAction();
        }
    }

    /**
     * Replenish ODA Data.
     *
     * @param tokenKey TokenKey that identifies the certificate.
     */
    public void replenishODAData(final TokenKey tokenKey) {
        try {
            if (tokenKey != null) {
                long expirationTime, currentTimeStamp;
                VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                currentTimeStamp = System.currentTimeMillis() / 1000;
                expirationTime = visaPaymentSDK.getODAExpirationTime(tokenKey);

                if (currentTimeStamp > expirationTime) {
                    digitizationVts.replenishODADataRequest(tokenKey, new DigitizationListener() {
                        @Override
                        public void onStarted() {
                        }

                        @Override
                        public void onError(SdkError sdkError) {
                        }

                        @Override
                        public void onApproved() {
                        }

                        @Override
                        public void onDeclined() {
                        }

                        @Override
                        public void onRequireAdditionalAuthentication(String tokenUniqueReference, AuthenticationMethod[] authenticationMethods) {
                        }
                    });
                }
            }
        } catch (VisaPaymentSDKException e) {
            e.printStackTrace();
        }
    }
}
