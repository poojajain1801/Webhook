package com.comviva.hceservice.digitizationApi;

import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.digitizationApi.asset.AssetType;
import com.comviva.hceservice.digitizationApi.asset.GetAssetResponse;
import com.comviva.hceservice.digitizationApi.asset.MediaContent;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.LuhnUtil;
import com.comviva.hceservice.util.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.MdesMcbpWalletApi;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.remotemanagement.mdes.RemoteManagementHandler;
import com.visa.cbp.external.common.DynParams;
import com.visa.cbp.external.common.ExpirationDate;
import com.visa.cbp.external.common.HceData;
import com.visa.cbp.external.common.TokenInfo;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.TokenStatus;
import com.visa.cbp.sdk.facade.exception.VisaPaymentSDKException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import flexjson.JSONDeserializer;

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
        byte[] baMobKeySetId = null;
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            baMobKeySetId = RemoteManagementHandler.getInstance().getLdeRemoteManagementService().getMobileKeySetIdAsByteArray().getBytes();
            requestSessionReq.put("paymentAppProviderId", /*comvivaSdk.getPaymentAppProviderId()*/"ComvivaWallet");
            requestSessionReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            requestSessionReq.put("mobileKeysetId", new String(baMobKeySetId));
        } catch (JSONException | SdkException e) {
            Log.d("ComvivaSdkError", e.getMessage());
            return;
        } finally {
            if (baMobKeySetId != null) {
                Arrays.fill(baMobKeySetId, Constants.DEFAULT_FILL_VALUE);
            }
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
                    break;

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
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            jsReqActCodeReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            jsReqActCodeReq.put("tokenUniqueReference", tokenUniqueReference);

            JSONObject jsAuthenticationMethod = new JSONObject();
            jsAuthenticationMethod.put("id", authenticationMethod.getId());
            jsAuthenticationMethod.put("type", authenticationMethod.getType().name());
            jsAuthenticationMethod.put("value", authenticationMethod.getValue());
            jsReqActCodeReq.put("authenticationMethod", jsAuthenticationMethod);
        } catch (JSONException e) {
            activationCodeListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            activationCodeListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
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
     * Checks token's current status and update accordingly.
     * <p>
     * Note- This API is only applicable for VISA .
     *
     * @param paymentCard      Payment Card need to be checked
     * @param responseListener Listener
     */
    public void getTokenStatus(final PaymentCard paymentCard, final ResponseListener responseListener) {
        final TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
        final JSONObject jsonTokenStatusRequest = new JSONObject();
        try {
            jsonTokenStatusRequest.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), tokenData.getVProvisionedTokenID());
        } catch (Exception e) {
            //checkEligibilityListener.onCheckEligibilityError("Error while preparing request");
            return;
        }

        class GetTokenStatus extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (responseListener != null) {
                    responseListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSTokenStatus(), jsonTokenStatusRequest.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject jsGetTokenResponse = new JSONObject(httpResponse.getResponse());
                        if (jsGetTokenResponse.has(Tags.RESPONSE_CODE.getTag()) &&
                                !jsGetTokenResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            responseListener.onError(SdkErrorImpl.getInstance(jsGetTokenResponse.getInt(Tags.RESPONSE_CODE.getTag()),
                                    jsGetTokenResponse.getString("message")));
                            return;
                        }
                        if (jsGetTokenResponse.has(Tags.RESPONSE_CODE.getTag()) && jsGetTokenResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            parseGetTokenResponse(jsGetTokenResponse, tokenData.getTokenKey());
                            if (responseListener != null) {
                                responseListener.onSuccess();
                            }
                        }
                    } catch (JSONException e) {
                        responseListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } else {
                    responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }
            }
        }

        GetTokenStatus getTokenStatus = new GetTokenStatus();
        getTokenStatus.execute();
    }

    /**
     * This API allows clients to retrieve metadata related to the token.
     * <p>
     * Note- This API is only applicable for VISA .
     *
     * @param paymentCard      Payment Card need to be checked
     * @param responseListener Listener
     */

    public void getCardMetaData(final PaymentCard paymentCard, final ResponseListener responseListener) {
        final TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
        final JSONObject jsonCardMetaDataRequest = new JSONObject();
        try {
            //jsonCardMetaDataRequest.put(Tags.VPAN_ENROLLMENT_ID.getTag(), paymentCard.pr);
        } catch (Exception e) {
            responseListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }
        class GetCardMetaDataTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (responseListener != null) {
                    responseListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSCardMetaDataUrl(), jsonCardMetaDataRequest.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject jsGetCardMetaDataResponse = new JSONObject(httpResponse.getResponse());
                        if (jsGetCardMetaDataResponse.has(Tags.RESPONSE_CODE.getTag()) && !jsGetCardMetaDataResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            responseListener.onError(SdkErrorImpl.getInstance(jsGetCardMetaDataResponse.getInt(Tags.RESPONSE_CODE.getTag()),
                                    jsGetCardMetaDataResponse.getString("message")));
                            return;
                        }
                        if (jsGetCardMetaDataResponse.has(Tags.RESPONSE_CODE.getTag()) && jsGetCardMetaDataResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            parseGetMetaDataResponse(jsGetCardMetaDataResponse);
                            if (responseListener != null) {
                                responseListener.onSuccess();
                            }
                        }
                    } catch (JSONException e) {
                        responseListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } else {
                    responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }
            }
        }

        GetCardMetaDataTask getCardMetaDataTask = new GetCardMetaDataTask();
        getCardMetaDataTask.execute();
    }

    private void parseGetMetaDataResponse(JSONObject jsGetMetaDataResponse) throws JSONException {
        JSONObject jsPaymentInstrument = jsGetMetaDataResponse.getJSONObject("paymentInstrument");
        JSONObject jsExpirationDateObject = jsPaymentInstrument.getJSONObject("expirationDate");
        JSONObject jsCardMetaDataResponseObject = jsGetMetaDataResponse.getJSONObject("cardMetaData");
        JSONArray jsTokensArray = jsGetMetaDataResponse.getJSONArray("tokens");
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        for (int i = 0; i < jsTokensArray.length(); i++) {
            TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(jsTokensArray.getJSONObject(i).getString("vProvisionedTokenID"));
            visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.getTokenStatus(jsTokensArray.getJSONObject(i).getString("tokenStatus")));
        }

        String last4 = jsPaymentInstrument.getString("last4");
        String paymentAccountReference = jsPaymentInstrument.getString("paymentAccountReference");

        // Card MetaData
        com.comviva.hceservice.digitizationApi.CardMetaData cardMetaData = new com.comviva.hceservice.digitizationApi.CardMetaData();
        cardMetaData.setLongDescription(jsCardMetaDataResponseObject.getString("longDescription"));
        cardMetaData.setBackgroundColor(jsCardMetaDataResponseObject.getString("backgroundColor"));
        cardMetaData.setContactEmail(jsCardMetaDataResponseObject.getString("contactEmail"));
        cardMetaData.setContactName(jsCardMetaDataResponseObject.getString("contactName"));
        cardMetaData.setContactNumber(jsCardMetaDataResponseObject.getString("contactNumber"));
        cardMetaData.setForegroundColor(jsCardMetaDataResponseObject.getString("foregroundColor"));
        cardMetaData.setContactWebsite(jsCardMetaDataResponseObject.getString("contactWebsite"));
        cardMetaData.setShortDescription(jsCardMetaDataResponseObject.getString("shortDescription"));
        cardMetaData.setLabelColor(jsCardMetaDataResponseObject.getString("labelColor"));
        cardMetaData.setTermsAndConditionsID(jsCardMetaDataResponseObject.getString("termsAndConditionsID"));
    }


    private TokenInfo parseGetTokenResponse(JSONObject jsGetTokenResponse, TokenKey tokenKey) throws JSONException {

        JSONObject jsTokenInfoObject = jsGetTokenResponse.getJSONObject("tokenInfo");
        JSONObject jsExpirationDateObject = jsTokenInfoObject.getJSONObject("expirationDate");
        JSONObject jsHCEDataObject = jsTokenInfoObject.getJSONObject("hceData");
        JSONObject jsDynParamsObject = jsHCEDataObject.getJSONObject("dynParams");


        ExpirationDate expirationDate = new ExpirationDate();
        expirationDate.setMonth(jsExpirationDateObject.getString("month"));
        expirationDate.setYear(jsExpirationDateObject.getString("year"));
        com.visa.cbp.external.common.ParamsStatus paramsStatus = (com.visa.cbp.external.common.ParamsStatus) new JSONDeserializer<>().deserialize(jsDynParamsObject.toString(), com.visa.cbp.external.common.ParamsStatus.class);
        /*com.visa.cbp.external.common.ParamsStatus paramsStatus = new com.visa.cbp.external.common.ParamsStatus();
        paramsStatus.*/

        DynParams dynParams = new DynParams();
        dynParams.setParamsStatus(paramsStatus);
        dynParams.setApi(jsDynParamsObject.getString("api"));

        HceData hceData = new HceData();
        hceData.setDynParams(dynParams);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setHceData(hceData);
        tokenInfo.setExpirationDate(expirationDate);
        tokenInfo.setTokenStatus(jsTokenInfoObject.getString("tokenStatus"));

        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.getTokenStatus(jsTokenInfoObject.getString("tokenStatus")));
        return tokenInfo;
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
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
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
        } catch (SdkException e) {
            activateListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
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
     * Set Mobile PIN if it is not already set.
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
     * @param paymentCard Payment Card need to be checked.
     */
    public void replenishODAData(final PaymentCard paymentCard) {

        final TokenKey tokenKey = ((TokenData) paymentCard.getCurrentCard()).getTokenKey();
        try {
            if (tokenKey != null) {
                long expirationTime, currentTimeStamp;
                VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                currentTimeStamp = System.currentTimeMillis() / 1000;
                expirationTime = visaPaymentSDK.getODAExpirationTime(tokenKey);

                if (currentTimeStamp > expirationTime) {
                    digitizationVts = new DigitizationVts();
                    digitizationVts.replenishODADataRequest(paymentCard, new DigitizationListener() {
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

    /**
     * Replenish Transaction Credential for the given token.
     *
     * @param paymentCard Token to be replenished
     * @param listener    UI Lister
     */
    public void replenishTransactionCredential(PaymentCard paymentCard, ResponseListener listener) {
        switch (paymentCard.getCardType()) {
            case VTS:
                DigitizationVts digitizationVts = new DigitizationVts();
                digitizationVts.replenishLuk(paymentCard, listener);
                break;

            case MDES:
                ComvivaSdk comvivaSdk;
                try {
                    comvivaSdk = ComvivaSdk.getInstance(null);
                    comvivaSdk.replenishCard(paymentCard.getCardUniqueId());
                } catch (SdkException e) {

                }
                break;

            default:
                // Unsupported Card
        }
    }

    /**
     * Fetches content value of the given GUID identified by GUID.
     *
     * @param guid             GUID of the resource
     * @param getAssetListener Listener
     */
    public void getContent(final String guid, GetAssetListener getAssetListener) {
        if (digitizationVts == null) {
            digitizationVts = new DigitizationVts();
        }
        digitizationVts.getContent(guid, getAssetListener);
    }

}
