package com.comviva.hceservice.mdes.digitizatioApi;

import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.common.RmPendingTask;
import com.comviva.hceservice.common.database.CommonDb;
import com.comviva.hceservice.mdes.digitizatioApi.asset.AssetType;
import com.comviva.hceservice.mdes.digitizatioApi.asset.GetAssetResponse;
import com.comviva.hceservice.mdes.digitizatioApi.asset.MediaContent;
import com.comviva.hceservice.mdes.digitizatioApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.mdes.digitizatioApi.authentication.AuthenticationType;
import com.comviva.hceservice.util.ArrayUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;

/**
 * Contains all Digitization APIs.
 * Created by tarkeshwar.v on 5/23/2017.
 */
public class Digitization {
    private JSONObject prepareCardInfo(CardEligibilityRequest cardEligibilityRequest) throws JSONException,
            GeneralSecurityException, IOException {
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
        byte[] oneTimeAesKey = ArrayUtil.getRandomNumber(16);
        byte[] oneTimeIv = ArrayUtil.getRandomNumber(16);
        byte[] baEncryptedData = AESUtil.cipherCBC(cardInfoData.toString().getBytes(), oneTimeAesKey,
                oneTimeIv, AESUtil.Padding.PKCS5Padding, true);

        // Encrypting AES key with Mastercard public key
        RSAPublicKey masterPubKey = CertificateUtil.getRsaPublicKey("mastercard_public.cer",
                ComvivaHce.getInstance(null).getApplicationContext());
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, masterPubKey);
        byte[] encryptedKey = cipher.doFinal(oneTimeAesKey);

        // Preparing Card Info
        cardInfo.put("encryptedData", ArrayUtil.getHexString(baEncryptedData));
        cardInfo.put("encryptedKey", ArrayUtil.getHexString(encryptedKey));
        cardInfo.put("iv", ArrayUtil.getHexString(oneTimeIv));
        // TODO
        cardInfo.put("publicKeyFingerPrint", "");
        return cardInfo;
    }

    public void checkCardEligibility(CardEligibilityRequest cardEligibilityRequest, final CheckCardEligibilityListener checkEligibilityListener) {
        final JSONObject jsonCardEligibilityReq = new JSONObject();
        try {
            ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
            JSONObject cardInfoData = prepareCardInfo(cardEligibilityRequest);

            jsonCardEligibilityReq.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            jsonCardEligibilityReq.put("paymentAppId", comvivaHce.getPaymentAppProviderId());
            jsonCardEligibilityReq.put("tokenType", "CLOUD");
            jsonCardEligibilityReq.put("cardInfo", cardInfoData);
            jsonCardEligibilityReq.put("cardletId", "1.0");
        } catch (JSONException e) {
        } catch (GeneralSecurityException e) {
        } catch (IOException e) {
        }

        class CheckCardEligibilityTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (checkEligibilityListener != null) {
                    checkEligibilityListener.onCheckEligibilityStarted();
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
                        JSONObject respObj = (new JSONObject(httpResponse.getResponse())).getJSONObject("response");

                        // Card is not eligible
                        if (!respObj.has("eligibilityReceipt")) {
                            checkEligibilityListener.onCheckEligibilityError("Card is not eligible");
                        } else {
                            // Card is eligible
                            JSONObject jsEligibilityReceipt = respObj.getJSONObject("eligibilityReceipt");
                            JSONObject jsApplicableCardInfo = respObj.getJSONObject("applicableCardInfo");

                            CardEligibilityResponse cardEligibilityResponse = new CardEligibilityResponse();
                            cardEligibilityResponse.setResponseHost(respObj.getString("responseHost"));
                            cardEligibilityResponse.setResponseId(respObj.getString("responseId"));
                            cardEligibilityResponse.setTermsAndConditionsAssetId(respObj.getString("termsAndConditionsAssetId"));
                            cardEligibilityResponse.setServiceId(respObj.getString("serviceId"));
                            cardEligibilityResponse.getEligibilityReceipt().setValue(jsEligibilityReceipt.getString("value"));
                            cardEligibilityResponse.getEligibilityReceipt().setValidForMinutes(jsEligibilityReceipt.getInt("validForMinutes"));
                            cardEligibilityResponse.getApplicableCardInfo().setSecurityCodeApplicable(jsApplicableCardInfo.getBoolean("isSecurityCodeApplicable"));
                            checkEligibilityListener.onTermsAndConditionsRequired(cardEligibilityResponse);
                        }
                    } else {
                        checkEligibilityListener.onCheckEligibilityError(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                }
            }
        }

        CheckCardEligibilityTask checkCardEligibilityTask = new CheckCardEligibilityTask();
        checkCardEligibilityTask.execute();
    }

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
        }
        return getAssetResponse;
    }

    public void digitize(DigitizationRequest digitizationRequest, final DigitizationListener digitizationListener) {
        final ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        final JSONObject jsonContinueDigitizationReq = new JSONObject();
        try {
            JSONObject jsEligibilityReceipt = new JSONObject();
            EligibilityReceipt eligibilityReceipt = digitizationRequest.getEligibilityReceipt();
            jsEligibilityReceipt.put("value", eligibilityReceipt.getValue());
            jsEligibilityReceipt.put("validForMinutes", eligibilityReceipt.getValidForMinutes());

            jsonContinueDigitizationReq.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            jsonContinueDigitizationReq.put("eligibilityReceipt", jsEligibilityReceipt);
            jsonContinueDigitizationReq.put("taskId", digitizationRequest.getTaskId());
            jsonContinueDigitizationReq.put("serviceId", digitizationRequest.getServiceId());
            jsonContinueDigitizationReq.put("termsAndConditionsAssetId", digitizationRequest.getTermsAndConditionsAssetId());
            jsonContinueDigitizationReq.put("termsAndConditionsAcceptedTimestamp", digitizationRequest.getTermsAndConditionsAcceptedTimestamp());
        } catch (JSONException e) {
        }

        class DigitizeTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (digitizationListener != null) {
                    digitizationListener.onDigitizationStarted();
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
                if (digitizationListener != null) {
                    digitizationListener.onDigitizationCompleted();
                }

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject respObj = (new JSONObject(httpResponse.getResponse())).getJSONObject("response");

                        // First Check decision, it's APPROVED, DECLINED or REQUIRE_ADDITIONAL_AUTHENTICATION
                        final String decision = respObj.getString("decision");
                        if (decision.equalsIgnoreCase("DECLINED")) {
                            digitizationListener.onDeclined();
                            return;
                        }

                        final String tokenUniqueReference = respObj.getString("tokenUniqueReference");
                        final String panUniqueReference = respObj.getString("panUniqueReference");

                        // TODO need to think, where to store
                        // authenticationMethods auth req only
                        // productConfig Obj
                        // tokenInfo Obj
                        // tdsRegistrationUrl conditional if supported

                        // Insert this task into pending list
                        RmPendingTask rmPendingTask = new RmPendingTask();
                        rmPendingTask.setTaskId(CommonDb.RM_T_ID_001);
                        rmPendingTask.setTokenUniqueReference(tokenUniqueReference);
                        comvivaHce.getCommonDb().saveRmPendingTask(rmPendingTask);

                        if (decision.equalsIgnoreCase("APPROVED")) {
                            digitizationListener.onApproved();
                            return;
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
                            digitizationListener.onRequireAdditionalAuthentication(tokenUniqueReference, authenticationMethods);
                        }
                    } else {
                        digitizationListener.onError(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                    digitizationListener.onError("Wrong data from server");
                }
            }
        }

        DigitizeTask digitizeTask = new DigitizeTask();
        digitizeTask.execute();
    }

    public void requestSession() {
        final JSONObject requestSessionReq = new JSONObject();
        try {
            byte[] baMobKeySetId = RemoteManagementHandler.getInstance().getLdeRemoteManagementService().getMobileKeySetIdAsByteArray().getBytes();
            ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
            requestSessionReq.put("paymentAppProviderId", /*comvivaHce.getPaymentAppProviderId()*/"ComvivaWallet");
            requestSessionReq.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            requestSessionReq.put("mobileKeysetId", new String(baMobKeySetId));
        } catch (JSONException e) {
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

    public void performCardLcm(final CardLcmRequest cardLcmRequest, final CardLcmListener cardLcmListener) {
        final JSONObject jsCardLcmReq = new JSONObject();
        try {
            ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
            jsCardLcmReq.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());

            ArrayList cardList = cardLcmRequest.getTokenUniqueReferences();
            JSONArray jsArrCards = new JSONArray();
            int noOfCard = cardList.size();
            for (int i = 0; i < noOfCard; i++) {
                jsArrCards.put(cardList.get(i));
            }
            jsCardLcmReq.put("tokenUniqueReferences", jsArrCards);
            jsCardLcmReq.put("causedBy", "CARDHOLDER");
            jsCardLcmReq.put("reasonCode", cardLcmRequest.getReasonCode().name());
            jsCardLcmReq.put("reason", "Not Specified");

            jsCardLcmReq.put("operation", cardLcmRequest.getCardLcmOperation().name());
        } catch (JSONException e) {
        }

        class CardLcmTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cardLcmListener.onCardLcmStarted();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getDeleteCardUrl(), jsCardLcmReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        // Get all tokens
                        JSONObject jsResponse = new JSONObject(httpResponse.getResponse());
                        JSONArray tokens = jsResponse.getJSONArray("tokens");
                        JSONObject token;
                        String tokenUniqueRef;
                        for (int i = 0; i < tokens.length(); i++) {
                            token = tokens.getJSONObject(i);
                            tokenUniqueRef = token.getString("tokenUniqueReference");
                            switch (cardLcmRequest.getCardLcmOperation()) {
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
                    } catch (JSONException | AlreadyInProcessException e) {
                        e.printStackTrace();
                    }
                } else {
                    cardLcmListener.onError(httpResponse.getResponse());
                }
            }
        }
        CardLcmTask cardLcmTask = new CardLcmTask();
        cardLcmTask.execute();
    }

    public void requestActivationCode(final String tokenUniqueReference,
                                      final AuthenticationMethod authenticationMethod,
                                      final RequestActivationCodeListener activationCodeListener) {
        final JSONObject jsReqActCodeReq = new JSONObject();
        try {
            ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
            jsReqActCodeReq.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
            jsReqActCodeReq.put("tokenUniqueReference", tokenUniqueReference);

            JSONObject jsAuthenticationMethod = new JSONObject();
            jsAuthenticationMethod.put("id", authenticationMethod.getId());
            jsAuthenticationMethod.put("type", authenticationMethod.getType().name());
            jsAuthenticationMethod.put("value", authenticationMethod.getValue());
            jsReqActCodeReq.put("authenticationMethod", authenticationMethod);
        } catch (JSONException e) {
        }

        class ReqActivationCodeTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activationCodeListener.onReqActivationCodeStarted();
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
                        activationCodeListener.onError(httpResponse.getResponse());
                    }
                } catch (Exception e) {
                }
            }
        }
        ReqActivationCodeTask reqActivationCodeTask = new ReqActivationCodeTask();
        reqActivationCodeTask.execute();
    }

    public void activate(final String tokenUniqueReference, final String activationCode, ActivationCodeType type,
                         final ActivateListener activateListener) {
        final JSONObject jsActivateReq = new JSONObject();
        try {
            ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
            jsActivateReq.put("paymentAppInstanceId", comvivaHce.getPaymentAppInstanceId());
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
        }

        class ActivateTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activateListener.onActivationStarted();
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
                        activateListener.onError(httpResponse.getResponse());
                    }
                } catch (Exception e) {
                }
            }
        }
        ActivateTask activateTask = new ActivateTask();
        activateTask.execute();
    }
}
