package com.comviva.hceservice.digitizationApi;

import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.digitizationApi.asset.AssetType;
import com.comviva.hceservice.digitizationApi.asset.GetAssetResponse;
import com.comviva.hceservice.digitizationApi.asset.MediaContent;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationType;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.UrlUtil;
import com.comviva.hceservice.util.crypto.AESUtil;
import com.comviva.hceservice.util.crypto.CertificateUtil;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.remotemanagement.mdes.RemoteManagementHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;

class DigitizationMdes {
    private CardEligibilityResponse cardEligibilityResponse;
    private static final String RESPONSE = "response";
    private static final String PAYMENT_APP_INSTANE_ID = "paymentAppInstanceId";
    private static final String T_N_C = "termsAndConditionsAssetId";
    private static final String RESPONSE_CODE = "reasonCode";
    private static final String ELIIBILITY_RECEIPT = "eligibilityReceipt";

    private JSONObject prepareCardInfo(CardEligibilityRequest cardEligibilityRequest) throws JSONException,
            GeneralSecurityException, IOException, SdkException {
        byte[] oneTimeAesKey = null;
        byte[] oneTimeIv = null;
        byte[] encryptedKey = null;
        byte[] baEncryptedData = null;
        RSAPublicKey masterPubKey = null;
        try {
            JSONObject cardInfo = new JSONObject();
            JSONObject cardInfoData = new JSONObject();
            // Preparing Card Info Data
            cardInfoData.put("accountNumber", cardEligibilityRequest.getAccountNumber());
            cardInfoData.put("expiryMonth", cardEligibilityRequest.getExpiryMonth());
            cardInfoData.put("expiryYear", cardEligibilityRequest.getExpiryYear());
            cardInfoData.put("source", cardEligibilityRequest.getSource());
            cardInfoData.put("cardholderName", cardEligibilityRequest.getCardholderName());
            cardInfoData.put("securityCode", cardEligibilityRequest.getSecurityCode());

            // Generating one time AES key & IV and encrypting card info data with AES key
            oneTimeAesKey = ArrayUtil.getRandomNumber(16);
            oneTimeIv = ArrayUtil.getRandomNumber(16);
            baEncryptedData = AESUtil.cipherCBC(cardInfoData.toString().getBytes(), oneTimeAesKey,
                    oneTimeIv, AESUtil.Padding.PKCS5Padding, true);

            // Encrypting AES key with Mastercard public key
            masterPubKey = CertificateUtil.getRsaPublicKey("mastercard_public.cer",
                    ComvivaSdk.getInstance(null).getApplicationContext());
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, masterPubKey);
            encryptedKey = cipher.doFinal(oneTimeAesKey);

            // Preparing Card Info
            cardInfo.put("encryptedData", ArrayUtil.getHexString(baEncryptedData));
            cardInfo.put("encryptedKey", ArrayUtil.getHexString(encryptedKey));
            cardInfo.put("iv", ArrayUtil.getHexString(oneTimeIv));

            cardInfo.put("publicKeyFingerPrint", "");
            return cardInfo;
        } finally {
            if (oneTimeAesKey != null) {
                Arrays.fill(oneTimeAesKey, Constants.DEFAULT_FILL_VALUE);
            }
            if (oneTimeIv != null) {
                Arrays.fill(oneTimeIv, Constants.DEFAULT_FILL_VALUE);
            }
            if (encryptedKey != null) {
                Arrays.fill(encryptedKey, Constants.DEFAULT_FILL_VALUE);
            }
            if (baEncryptedData != null) {
                Arrays.fill(baEncryptedData, Constants.DEFAULT_FILL_VALUE);
            }
        }
    }

    private class GetTnCAssetTask extends AsyncTask<Void, Void, HttpResponse> {
        private Map<String, String> queryMap;
        private GetAssetListener listener;

        GetTnCAssetTask(String assetId, GetAssetListener listener) {
            queryMap = new HashMap<>();
            queryMap.put("assetId", assetId);
            this.listener = listener;
        }

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
                    JSONObject respObj = (new JSONObject(httpResponse.getResponse())).getJSONObject(RESPONSE);
                    JSONArray arrMediaContents = respObj.getJSONArray("mediaContents");
                    int noOfAssets = arrMediaContents.length();
                    MediaContent[] mediaContents = new MediaContent[noOfAssets];
                    JSONObject jsMediaContent;
                    for (int i = 0; i < arrMediaContents.length(); i++) {
                        mediaContents[i] = new MediaContent();
                        jsMediaContent = (JSONObject) arrMediaContents.get(i);
                        String type = jsMediaContent.getString("type");
                        AssetType assetType = AssetType.getType(type);
                        mediaContents[i].setAssetType(assetType);
                        mediaContents[i].setData(jsMediaContent.getString("data"));
                        switch (assetType) {
                            // Height and Width is only application for image only
                            case IMAGE_PNG:
                                mediaContents[i].setHeight(Integer.parseInt(jsMediaContent.getString("height")));
                                mediaContents[i].setWidth(Integer.parseInt(jsMediaContent.getString("width")));
                                break;
                            default:
                                break;
                        }
                    }
                    ContentGuid contentGuid = new ContentGuid();
                    contentGuid.setContent(mediaContents);
                    listener.onCompleted(contentGuid);
                } else {
                    listener.onError(httpResponse.getResponse());
                }
            } catch (JSONException e) {
                listener.onError("Wrong data from server");
            }
        }
    }

    CardEligibilityResponse getCardEligibilityResponse() {
        return cardEligibilityResponse;
    }

    /**
     * Checks that Card is eligible for digitization or not.
     *
     * @param cardEligibilityRequest   Eligibility request
     * @param checkEligibilityListener Eligibility Response
     */
    void checkCardEligibilityMdes(CardEligibilityRequest cardEligibilityRequest, final CheckCardEligibilityListener checkEligibilityListener) {
        final JSONObject jsonCardEligibilityReq = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            JSONObject cardInfoData = prepareCardInfo(cardEligibilityRequest);

            jsonCardEligibilityReq.put(PAYMENT_APP_INSTANE_ID, comvivaSdk.getPaymentAppInstanceId());
            jsonCardEligibilityReq.put("paymentAppId", comvivaSdk.getPaymentAppProviderId());
            jsonCardEligibilityReq.put("tokenType", "CLOUD");
            jsonCardEligibilityReq.put("cardInfo", cardInfoData);
            jsonCardEligibilityReq.put("cardletId", "1.0");
        } catch (JSONException e) {
            if (checkEligibilityListener != null) {
                checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            }
        } catch (GeneralSecurityException e) {
            if (checkEligibilityListener != null) {
                checkEligibilityListener.onError(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
            }
        } catch (IOException e) {
            if (checkEligibilityListener != null) {
                checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_IO_ERROR);
            }
        } catch (SdkException e) {
            if (checkEligibilityListener != null) {
                checkEligibilityListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            }
        }

        class CheckCardEligibilityTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (checkEligibilityListener != null) {
                    checkEligibilityListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getCheckCardEligibilityUrl(), jsonCardEligibilityReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (checkEligibilityListener != null) {
                    checkEligibilityListener.onCheckEligibilityCompleted();
                }

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = (new JSONObject(httpResponse.getResponse())).getJSONObject(RESPONSE);

                        // Card is not eligible
                        if (!respObj.has(ELIIBILITY_RECEIPT)) {
                            if (checkEligibilityListener != null) {
                                checkEligibilityListener.onError(SdkErrorStandardImpl.COMMON_CARD_NOT_ELIGIBLE);
                            }
                        } else {
                            // Card is eligible
                            JSONObject jsEligibilityReceipt = respObj.getJSONObject(ELIIBILITY_RECEIPT);
                            JSONObject jsApplicableCardInfo = respObj.getJSONObject("applicableCardInfo");

                            cardEligibilityResponse = new CardEligibilityResponse();
                            cardEligibilityResponse.setResponseHost(respObj.getString("responseHost"));
                            cardEligibilityResponse.setResponseId(respObj.getString("responseId"));
                            cardEligibilityResponse.setServiceId(respObj.getString("serviceId"));
                            cardEligibilityResponse.getEligibilityReceipt().setValue(jsEligibilityReceipt.getString("value"));
                            cardEligibilityResponse.getEligibilityReceipt().setValidForMinutes(jsEligibilityReceipt.getInt("validForMinutes"));
                            cardEligibilityResponse.getApplicableCardInfo().setSecurityCodeApplicable(jsApplicableCardInfo.getBoolean("isSecurityCodeApplicable"));

                            // Perform TnC if required
                            String tncAssetId;
                            if (respObj.has(T_N_C)) {
                                GetAssetListener getAssetListener = new GetAssetListener() {
                                    @Override
                                    public void onStarted() {
                                    }

                                    @Override
                                    public void onCompleted(ContentGuid contentGuid) {
                                        if (checkEligibilityListener != null) {
                                            checkEligibilityListener.onTermsAndConditionsRequired(contentGuid);
                                        }
                                    }

                                    @Override
                                    public void onError(String message) {
                                        if (checkEligibilityListener != null) {
                                            checkEligibilityListener.onError(SdkErrorImpl.getInstance(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR.getErrorCode(), message));
                                        }
                                    }
                                };

                                tncAssetId = respObj.getString(T_N_C);
                                cardEligibilityResponse.setTermsAndConditionsAssetId(tncAssetId);
                                GetTnCAssetTask getTnCAssetTask = new GetTnCAssetTask(tncAssetId, getAssetListener);
                                getTnCAssetTask.execute();
                                return;
                            }
                            if (checkEligibilityListener != null) {
                                checkEligibilityListener.onCheckEligibilityCompleted();
                            }
                        }
                    } else {
                        if (checkEligibilityListener != null) {
                            checkEligibilityListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getResponse()));
                        }
                    }
                } catch (JSONException e) {
                    if (checkEligibilityListener != null) {
                        checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
                    }
                }
            }
        }

        CheckCardEligibilityTask checkCardEligibilityTask = new CheckCardEligibilityTask();
        checkCardEligibilityTask.execute();
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
            Log.d("ComvivaSdkError", e.getMessage());
        }
        return getAssetResponse;
    }

    /**
     * Digitize the card.
     *
     * @param digitizationRequest  Digitization Request
     * @param digitizationListener UI Listener
     */
    void digitize(DigitizationRequest digitizationRequest, final DigitizationListener digitizationListener) {
        final JSONObject jsonContinueDigitizationReq = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            JSONObject jsEligibilityReceipt = new JSONObject();
            EligibilityReceipt eligibilityReceipt = cardEligibilityResponse.getEligibilityReceipt();
            jsEligibilityReceipt.put("value", eligibilityReceipt.getValue());
            jsEligibilityReceipt.put("validForMinutes", eligibilityReceipt.getValidForMinutes());

            jsonContinueDigitizationReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());
            jsonContinueDigitizationReq.put(ELIIBILITY_RECEIPT, jsEligibilityReceipt);
            jsonContinueDigitizationReq.put("serviceId", cardEligibilityResponse.getServiceId());
            jsonContinueDigitizationReq.put(T_N_C, cardEligibilityResponse.getTermsAndConditionsAssetId());
            jsonContinueDigitizationReq.put("termsAndConditionsAcceptedTimestamp", digitizationRequest.getTermsAndConditionsAcceptedTimestamp());
        } catch (JSONException e) {
            digitizationListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            digitizationListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
        }

        class DigitizeTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (digitizationListener != null) {
                    digitizationListener.onStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getContinueDigitizationUrl(), jsonContinueDigitizationReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = (new JSONObject(httpResponse.getResponse())).getJSONObject(RESPONSE);

                        // First Check decision, it's APPROVED, DECLINED or REQUIRE_ADDITIONAL_AUTHENTICATION
                        final String decision = respObj.getString("decision");
                        if (decision.equalsIgnoreCase("DECLINED")) {
                            digitizationListener.onDeclined();
                            return;
                        }

                        final String tokenUniqueReference = respObj.getString("tokenUniqueReference");


                        if (decision.equalsIgnoreCase("APPROVED")) {
                            digitizationListener.onApproved(null);
                        } else if (decision.equalsIgnoreCase("REQUIRE_ADDITIONAL_AUTHENTICATION")) {
                            JSONArray arrAuthenticationMethods = respObj.getJSONArray("authenticationMethods");
                            int noOfAuthMethods = arrAuthenticationMethods.length();

                            AuthenticationMethod[] authenticationMethods = new AuthenticationMethod[noOfAuthMethods];
                            JSONObject jsAuthMethod;
                            for (int i = 0; i < noOfAuthMethods; i++) {
                                jsAuthMethod = arrAuthenticationMethods.getJSONObject(i);
                                authenticationMethods[i] = new AuthenticationMethod(jsAuthMethod.getInt("id"),
                                        AuthenticationType.valueOf(jsAuthMethod.getString("type")),
                                        jsAuthMethod.getString("value"));
                            }
                            digitizationListener.onRequireAdditionalAuthentication(null,null, null);
                        }
                    } else {
                        digitizationListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                } catch (JSONException e) {
                    digitizationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                }
            }
        }

        DigitizeTask digitizeTask = new DigitizeTask();
        digitizeTask.execute();
    }

    /**
     * Request new session to complete pending task.
     */
    public void requestSession() {
        byte[] baMobKeySetId = null;
        final JSONObject requestSessionReq = new JSONObject();
        try {
            baMobKeySetId = RemoteManagementHandler.getInstance().getLdeRemoteManagementService().getMobileKeySetIdAsByteArray().getBytes();
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
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
     * This API is used to Suspend, UnSuspend and Delete Token
     */
    void performCardLcm(ArrayList<PaymentCard> cardList,
                        final CardLcmOperation cardLcmOperation,
                        final CardLcmReasonCode reasonCode,
                        final CardLcmListener cardLcmListener) {
        final JSONObject jsCardLcmReq = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            jsCardLcmReq.put("paymentAppInstanceId", comvivaSdk.getPaymentAppInstanceId());

            JSONArray jsArrCards = new JSONArray();
            int noOfCard = cardList.size();
            for (int i = 0; i < noOfCard; i++) {
                jsArrCards.put(cardList.get(i).getCardUniqueId());
            }
            jsCardLcmReq.put("tokenUniqueReferences", jsArrCards);
            jsCardLcmReq.put("causedBy", "CARDHOLDER");
            jsCardLcmReq.put(RESPONSE_CODE, reasonCode.name());
            jsCardLcmReq.put("reason", "Not Specified");
            jsCardLcmReq.put("operation", (cardLcmOperation == CardLcmOperation.RESUME) ? "UNSUSPEND" : cardLcmOperation.name());
        } catch (JSONException e) {
            cardLcmListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            cardLcmListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
        }

        class CardLcmTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cardLcmListener.onStarted();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getCardLifeCycleManagementMdesUrl(), jsCardLcmReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        // Get all tokens
                        JSONObject jsResponse = new JSONObject(httpResponse.getResponse());
                        if (jsResponse.has("reasonCode") && !jsResponse.getString("reasonCode").equalsIgnoreCase("200")) {
                            cardLcmListener.onError(SdkErrorImpl.getInstance(jsResponse.getInt("reasonCode"), jsResponse.getString("message")));
                            return;
                        }

                        JSONArray tokens = jsResponse.getJSONArray("tokens");
                        JSONObject token;
                        String tokenUniqueRef;
                        for (int i = 0; i < tokens.length(); i++) {
                            token = tokens.getJSONObject(i);
                            tokenUniqueRef = token.getString("tokenUniqueReference");
                            switch (cardLcmOperation) {
                                case DELETE:
                                    cardLcmListener.onSuccess("Card will be Deleted Successfully");
                                    McbpCardApi.deleteCard(tokenUniqueRef, false);
                                    break;

                                case SUSPEND:
                                    if (token.getString("status").equals("SUSPENDED")) {
                                        McbpCardApi.suspendCard(tokenUniqueRef);
                                        McbpCardApi.remoteWipeSuksForCard(tokenUniqueRef);
                                    }
                                    cardLcmListener.onSuccess("Card is suspended successfully");
                                    break;

                                case RESUME:
                                    if (token.getString("status").equals("ACTIVE")) {
                                        McbpCardApi.activateCard(tokenUniqueRef);
                                    }
                                    cardLcmListener.onSuccess("Card is resumed successfully");
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        cardLcmListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    } catch (AlreadyInProcessException e) {
                        cardLcmListener.onError(SdkErrorStandardImpl.SDK_TASK_ALREADY_IN_PROGRESS);
                    }
                } else {
                    cardLcmListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }
            }
        }
        CardLcmTask cardLcmTask = new CardLcmTask();
        cardLcmTask.execute();
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
            Log.d("ComvivaSdkError", e.getMessage());
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
                    Log.d("ComvivaSdkError", e.getMessage());
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
            Log.d("ComvivaSdkError", e.getMessage());
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
                        activateListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getResponse()));
                    }
                } catch (Exception e) {
                    Log.d("ComvivaSdkError", e.getMessage());
                }
            }
        }
        ActivateTask activateTask = new ActivateTask();
        activateTask.execute();
    }

}
