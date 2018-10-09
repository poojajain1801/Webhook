package com.comviva.hceservice.digitizationApi;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.LukInfo;
import com.comviva.hceservice.apiCalls.NetworkApi;
import com.comviva.hceservice.common.CardState;
import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.listeners.CheckCardEligibilityListener;
import com.comviva.hceservice.listeners.DigitizationListener;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.GetCardMetaDataListener;
import com.comviva.hceservice.listeners.StepUpListener;
import com.comviva.hceservice.listeners.TokenDataUpdateListener;
import com.comviva.hceservice.pojo.CardLCMOperationResponse;
import com.comviva.hceservice.pojo.ConfirmProvisioningResponse;
import com.comviva.hceservice.pojo.GenerateOTPResponse;
import com.comviva.hceservice.pojo.StepUpResponse;
import com.comviva.hceservice.pojo.VerifyOTPResponse;
import com.comviva.hceservice.pojo.tokenstatusupdate.TokenUpdateResponse;
import com.comviva.hceservice.requestobjects.CardLcmRequestParam;
import com.comviva.hceservice.requestobjects.DigitizationRequestParam;
import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;
import com.comviva.hceservice.pojo.enrollpanVts.EnrollPanResponse;
import com.comviva.hceservice.pojo.gettermsandconditionvts.GetTermsAndCondtionVtsResponse;
import com.comviva.hceservice.requestobjects.CardEligibilityRequestParam;
import com.comviva.hceservice.responseobject.StepUpRequest;
import com.comviva.hceservice.responseobject.contentguid.AssetType;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;
import com.comviva.hceservice.responseobject.contentguid.MediaContent;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.google.gson.Gson;
import com.visa.cbp.external.aam.ReplenishAckRequest;
import com.visa.cbp.external.aam.ReplenishRequest;
import com.visa.cbp.external.aam.ReplenishResponse;
import com.visa.cbp.external.common.DynParams;
import com.visa.cbp.external.common.ExpirationDate;
import com.visa.cbp.external.common.HceData;
import com.visa.cbp.external.common.IccPubKeyCert;
import com.visa.cbp.external.common.ReplenishODAData;
import com.visa.cbp.external.common.ReplenishODAResponse;
import com.visa.cbp.external.common.TokenInfo;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.external.enp.ProvisionResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.TokenStatus;
import com.visa.cbp.sdk.facade.data.TvlEntry;
import com.visa.cbp.sdk.facade.error.SDKErrorType;
import com.visa.cbp.sdk.facade.exception.CryptoException;
import com.visa.cbp.sdk.facade.exception.RootDetectException;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import flexjson.JSONDeserializer;

class DigitizationVts implements ServerResponseListener {

    private SDKData sdkData;
    private NetworkApi networkApi;
    private EnrollPanResponse enrollPanResponse;
    private ProvisionResponse provisionResponse;
    private CardLcmRequestParam cardLcmRequestParam;
    private PaymentCard paymentCard;
    private ComvivaSdk comvivaSdk;
    private TokenData tokenData;
    final VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();


    public DigitizationVts() {

        networkApi = new NetworkApi();
        sdkData = SDKData.getInstance();
    }


    @Override
    public void onRequestCompleted(Object result, Object listener) {

        if (result instanceof EnrollPanResponse) {
            CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
            EnrollPanResponse enrollPanResponse = (EnrollPanResponse) result;
            handleEnrollPanResponse(enrollPanResponse, checkCardEligibilityListener);
        } else if (result instanceof CardLCMOperationResponse) {
            ResponseListener responseListener = (ResponseListener) listener;
            CardLCMOperationResponse cardLcmOperationResponse = (CardLCMOperationResponse) result;
            handleCardLCMSuccessResponse(cardLcmOperationResponse, responseListener);
        } else if (result instanceof GetTermsAndCondtionVtsResponse) {
            GetTermsAndCondtionVtsResponse getTermsAndCondtionVtsResponse = (GetTermsAndCondtionVtsResponse) result;
            handleTermsAndConditionVtsResponse(getTermsAndCondtionVtsResponse, listener);
        } else if (result instanceof TokenUpdateResponse) {
            TokenDataUpdateListener tokenDataUpdateListener = (TokenDataUpdateListener) listener;
            TokenUpdateResponse tokenUpdateResponse = (TokenUpdateResponse) result;
            handleTokenUpdateResponse(tokenUpdateResponse, tokenDataUpdateListener);
        } else if (result instanceof ProvisionResponse) {
            DigitizationListener digitizationListener = (DigitizationListener) listener;
            ProvisionResponse provisionResponse = (ProvisionResponse) result;
            handleProvisionResponse(provisionResponse, digitizationListener);
        } else if (result instanceof ConfirmProvisioningResponse) {
            DigitizationListener digitizationListener = (DigitizationListener) listener;
            ConfirmProvisioningResponse confirmProvisionResponse = (ConfirmProvisioningResponse) result;
            handleConfirmProvisionResponse(confirmProvisionResponse, digitizationListener);
        } else if (result instanceof com.visa.cbp.external.common.CardMetaData) {
            com.visa.cbp.external.common.CardMetaData cardMetaData = (com.visa.cbp.external.common.CardMetaData) result;
            // parseGetCardMetaData(cardMetaData)
        } else if (result instanceof StepUpResponse) {
            StepUpResponse stepUpResponse = (StepUpResponse) result;
            StepUpListener stepUpListener = (StepUpListener) listener;
            if (Constants.HTTP_RESPONSE_CODE_200.equals(stepUpResponse.getResponseCode())) {
                stepUpListener.onRequireAdditionalAuthentication(prepareStepUpResponseObject(stepUpResponse.getStepUpRequest()));
            } else {
                stepUpListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(stepUpResponse.getResponseCode()), stepUpResponse.getResponseMessage()));
            }
        } else if (result instanceof VerifyOTPResponse) {
            VerifyOTPResponse verifyOTPResponse = (VerifyOTPResponse) result;
            ResponseListener responseListener = (ResponseListener) listener;
            if (Constants.HTTP_RESPONSE_CODE_200.equals(verifyOTPResponse.getResponseCode())) {
                responseListener.onSuccess();
            } else {
                responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(verifyOTPResponse.getResponseCode()), verifyOTPResponse.getResponseMessage()));
            }
        } else if (result instanceof GenerateOTPResponse) {
            GenerateOTPResponse generateOTPResponse = (GenerateOTPResponse) result;
            ResponseListener responseListener = (ResponseListener) listener;
            if (Constants.HTTP_RESPONSE_CODE_200.equals(generateOTPResponse.getResponseCode())) {
                responseListener.onSuccess();
            } else {
                responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(generateOTPResponse.getResponseCode()), generateOTPResponse.getResponseMessage()));
            }
        }
    }


    @Override
    public void onRequestError(String message, Object listener) {

        handleError(listener, message);
    }


    public void ConfirmReplenishTask(ReplenishAckRequest replenishAckRequest, String vProvisionedTokenID, final ConfirmProvisionListener listener) {

        final JSONObject jsConfirmReplenishment = new JSONObject();
        try {
            jsConfirmReplenishment.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), vProvisionedTokenID);
            jsConfirmReplenishment.put(Tags.API.getTag(), replenishAckRequest.getTokenInfo().getHceData().getDynParams().getApi());
            jsConfirmReplenishment.put(Tags.SC.getTag(), replenishAckRequest.getTokenInfo().getHceData().getDynParams().getSc());
        } catch (JSONException e) {
            if (listener != null) {
                listener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            }
        }
        class ConfirmReplenishTask extends AsyncTask<Void, Void, HttpResponse> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }


            @Override
            protected HttpResponse doInBackground(Void... params) {

                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSConfirmReplenishTokenUrl(), jsConfirmReplenishment.toString());
            }


            @Override
            protected void onPostExecute(HttpResponse httpResponse) {

                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        listener.onCompleted();
                    } else {
                        listener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                } catch (Exception e) {
                    listener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        ConfirmReplenishTask confirmReplenishTask = new ConfirmReplenishTask();
        confirmReplenishTask.execute();
    }


    private ContentGuid parseGetContentResponse(GetTermsAndCondtionVtsResponse getTermsAndCondtionVtsResponse) {

        ContentGuid contentGuid = new ContentGuid();
        if (null != getTermsAndCondtionVtsResponse.getAltText()) {
            contentGuid.setAltText(getTermsAndCondtionVtsResponse.getAltText());
        }
        contentGuid.setContentType(AssetType.getType(getTermsAndCondtionVtsResponse.getContentType()));
        MediaContent[] mediaContents = new MediaContent[getTermsAndCondtionVtsResponse.getContent().size()];
        int i = 0;
        for (com.comviva.hceservice.pojo.gettermsandconditionvts.Content content : getTermsAndCondtionVtsResponse.getContent()) {
            mediaContents[i] = new MediaContent();
            mediaContents[i].setData(content.getEncodedData());
            AssetType contentType = content.getMimeType();
            if (contentType.name().equals(Tags.IMG_PDF.getTag())) {
                mediaContents[i].setAssetType(AssetType.APPLICATION_PDF);
            } else {
                mediaContents[i].setAssetType(contentType);
            }
            switch (mediaContents[i].getAssetType()) {
                case IMAGE_PNG:
                case APPLICATION_PDF:
                    mediaContents[i].setHeight(content.getHeight());
                    mediaContents[i].setWidth(content.getWidth());
            }
            i++;
        }
        contentGuid.setContent(mediaContents);
        return contentGuid;
    }


    private ReplenishODAResponse parseReplenishODADataResponse(JSONObject jsReplenishODAData) throws JSONException {

        JSONObject jsODAData = jsReplenishODAData.getJSONObject("ODAData");
        JSONObject jsICCPubKeyCert = jsODAData.getJSONObject("iccPubKeyCert");
        JSONObject jsExpirationDate = jsICCPubKeyCert.getJSONObject("expirationDate");
        IccPubKeyCert iccPubKeyCert = new IccPubKeyCert();
        ExpirationDate expirationDate = new ExpirationDate();
        expirationDate.setMonth(jsExpirationDate.getString("month"));
        expirationDate.setYear(jsExpirationDate.getString("year"));
        iccPubKeyCert.setExpirationDate(expirationDate);
        iccPubKeyCert.setCertificate(jsICCPubKeyCert.getString("certificate"));
        iccPubKeyCert.setExponent(jsICCPubKeyCert.getString("exponent"));
        iccPubKeyCert.setRemainder(jsICCPubKeyCert.getString("remainder"));
        ReplenishODAData replenishODAData = new ReplenishODAData();
        replenishODAData.setIccPubKeyCert(iccPubKeyCert);
        ReplenishODAResponse replenishODAResponse = new ReplenishODAResponse();
        replenishODAResponse.setODAData(replenishODAData);
        return replenishODAResponse;
    }


    private void parseGetMetaDataResponse(JSONObject jsGetMetaDataResponse) throws JSONException, Exception {

        Gson gson = new Gson();
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
        CardMetaData cardMetaData = null;
        if (jsGetMetaDataResponse.has("cardMetaData")) {
            cardMetaData = gson.fromJson(jsGetMetaDataResponse.get("cardMetaData").toString(), CardMetaData.class);
        }
    }


    private void parseGetPanData(JSONObject jsGetPanDataResponse) throws JSONException, Exception {

        JSONObject jsPaymentInstrument = jsGetPanDataResponse.getJSONObject("paymentInstrument");
        String last4 = jsPaymentInstrument.getString("last4");
        String cvv2PrintedInd = jsPaymentInstrument.getString("cvv2PrintedInd");
        String expDatePrintedInd = jsPaymentInstrument.getString("expDatePrintedInd");
    }


    /**
     * Enroll PAN with Vts.
     *
     * @param cardEligibilityRequestParam Eligibility request
     * @param checkEligibilityListener    Eligibility Response
     */
    public void enrollPanVts(final CardEligibilityRequestParam cardEligibilityRequestParam, final CheckCardEligibilityListener checkEligibilityListener) throws SdkException {

        checkEligibilityListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.enrollPanVts(cardEligibilityRequestParam, checkEligibilityListener);
    }


    /**
     * Provision a given token previously enrolled.
     *
     * @param digitizationRequestParam Digitization Request
     * @param digitizationListener     UI listener for Digitization
     */
    public void provisionToken(DigitizationRequestParam digitizationRequestParam, final DigitizationListener digitizationListener) throws SdkException {

        digitizationListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.provisionVts(digitizationRequestParam, enrollPanResponse, digitizationListener);
    }


    public void getTokenStatus(PaymentCard paymentCard, final TokenDataUpdateListener tokenDataUpdateListener) throws SdkException {

        TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
        this.tokenData = tokenData;
        tokenDataUpdateListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.tokenUpdateVts(paymentCard, tokenDataUpdateListener);
    }


    public void getCardMetaData(String vpanEnrollmentID, GetCardMetaDataListener getCardMetaDataListener) throws SdkException {

        getCardMetaDataListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.getCardMetaDataVts(vpanEnrollmentID, getCardMetaDataListener);
    }


    /**
     * Replenish ODA data.
     *
     * @param paymentCard          Payment Card need to be checked.
     * @param digitizationListener Listener
     */
    void replenishODADataRequest(final PaymentCard paymentCard, final DigitizationListener digitizationListener) {

        final TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
        final JSONObject replenishODADataObject = new JSONObject();
        try {
            replenishODADataObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), tokenData.getVProvisionedTokenID());
        } catch (JSONException e) {
            digitizationListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        }
        class ReplenishODADataTask extends AsyncTask<Void, Void, HttpResponse> {

            protected HttpResponse doInBackground(Void... params) {

                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSReplenishODADataTokenUrl(), replenishODADataObject.toString());
            }


            protected void onPostExecute(HttpResponse httpResponse) {

                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject replenishODADataResponse = new JSONObject(httpResponse.getResponse());
                        if (replenishODADataResponse.has(Tags.RESPONSE_CODE.getTag()) && !replenishODADataResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            digitizationListener.onError(SdkErrorImpl.getInstance(replenishODADataResponse.getInt(Tags.RESPONSE_CODE.getTag()), replenishODADataResponse.getString("message")));
                            return;
                        }
                        if (replenishODADataResponse.has(Tags.RESPONSE_CODE.getTag()) && replenishODADataResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                         /*   visaPaymentSDK.setPasscode("1234");
                            visaPaymentSDK.verifyPasscode("1234");*/
                            visaPaymentSDK.processODAReplenishResponse(tokenData.getTokenKey(), parseReplenishODADataResponse(replenishODADataResponse));
                            if (digitizationListener != null) {
                                ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                                SharedPreferences pref = sdkData.getContext().getSharedPreferences(Tags.VPAN_ENROLLMENT_ID.getTag(), sdkData.getContext().MODE_PRIVATE);
                                digitizationListener.onApproved(pref.getString(tokenData.getVProvisionedTokenID(), null), enrollPanResponse.getCardMetaData());
                            }
                        }
                    } catch (JSONException e) {
                        if (digitizationListener != null) {
                            digitizationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                        }
                    } catch (SdkException e) {
                        Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                    }
                } else {
                    if (digitizationListener != null) {
                        digitizationListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getResponse()));
                    }
                }
            }
        }
        ReplenishODADataTask replenishODADataTask = new ReplenishODADataTask();
        replenishODADataTask.execute();
    }


    /**
     * Fetches content value of the given GUID identified by GUID.
     *
     * @param guid             GUID of the resource
     * @param getAssetListener Listener
     */
    void getContent(final String guid, GetAssetListener getAssetListener) throws SdkException {

        getAssetListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.getTermsAndConditionVts(guid, null, getAssetListener);
    }


    void getStepUpOptions(String stepUpID, StepUpListener stepUpListener) throws SdkException {

        stepUpListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.stepUpOptions(stepUpID, stepUpListener);
    }


    void verifyOTP(CardType cardType, String provisionID, String otpValue, ResponseListener responseListener) throws SdkException {

        responseListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.verifyOtp(cardType, provisionID, otpValue, responseListener);
    }


    void generateOTP(CardType cardType, String provisionID, String stepUpRequestId, final ResponseListener responseListener) throws SdkException {

        responseListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.generateOTP(cardType, provisionID, stepUpRequestId, responseListener);
    }


    /**
     * This API is used to Suspend, UnSuspend and Delete Token
     */
    void performCardLcm(CardLcmRequestParam cardLcmRequestParam,
                        final ResponseListener responseListener) throws SdkException {

        responseListener.onStarted();
        this.cardLcmRequestParam = cardLcmRequestParam;
        this.paymentCard = cardLcmRequestParam.getPaymentCard();
        networkApi.setServerAuthenticateListener(this);
        networkApi.performCardLCMMdesVts(cardLcmRequestParam, responseListener);
    }


    public void getCardMetaData(final PaymentCard paymentCard, final ResponseListener responseListener) {

        final TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
        final JSONObject jsonCardMetaDataRequest = new JSONObject();
        try {
            jsonCardMetaDataRequest.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), tokenData.getVProvisionedTokenID());
            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
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
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsGetCardMetaDataResponse = new JSONObject(httpResponse.getResponse());
                        parseGetMetaDataResponse(jsGetCardMetaDataResponse);
                        if (responseListener != null) {
                            responseListener.onSuccess();
                        }
                    } else {
                        if (responseListener != null) {
                            responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                        }
                    }
                } catch (JSONException e) {
                    if (responseListener != null) {
                        responseListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } catch (Exception e) {
                    if (responseListener != null) {
                        responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                    }
                }
            }
        }
        GetCardMetaDataTask getCardMetaDataTask = new GetCardMetaDataTask();
        getCardMetaDataTask.execute();
    }


    public void getPanData(final String pan, final ResponseListener responseListener) {

        final JSONObject jsonGetPanDataRequest = new JSONObject();
        try {
            jsonGetPanDataRequest.put(Tags.ENC_PAYMENT_INSTRUMENT.getTag(), pan);
        } catch (Exception e) {
            responseListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }
        class GetPanDataTask extends AsyncTask<Void, Void, HttpResponse> {

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
                return httpUtil.postRequest(UrlUtil.getVTSPanData(), jsonGetPanDataRequest.toString());
            }


            @Override
            protected void onPostExecute(HttpResponse httpResponse) {

                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsGetCardMetaDataResponse = new JSONObject(httpResponse.getResponse());
                        parseGetPanData(jsGetCardMetaDataResponse);
                        if (responseListener != null) {
                            responseListener.onSuccess();
                        }
                    } else if (responseListener != null) {
                        responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                } catch (JSONException e) {
                    if (responseListener != null) {
                        responseListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } catch (Exception e) {
                    if (responseListener != null) {
                        responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                    }
                }
            }
        }
        GetPanDataTask getPanDataTask = new GetPanDataTask();
        getPanDataTask.execute();
    }


    /**
     * Replenish LUK for given token
     *
     * @param paymentCard Token to be replenished
     * @param listener    UI Listener
     */
    void replenishLuk(final PaymentCard paymentCard, final ResponseListener listener) {

        final VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        final TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
        final TokenKey tokenKey = tokenData.getTokenKey();
        final JSONObject jsReplenishReq = new JSONObject();
        try {
            ReplenishRequest replenishRequest = visaPaymentSDK.constructReplenishRequest(tokenKey);
            jsReplenishReq.put(Tags.V_PROVISIONED_TOKEN_ID_SMALLP.getTag(), tokenData.getVProvisionedTokenID());
            jsReplenishReq.put(Tags.MAC.getTag(), replenishRequest.getSignature().getMac());
            jsReplenishReq.put(Tags.API.getTag(), replenishRequest.getTokenInfo().getHceData().getDynParams().getApi());
            jsReplenishReq.put(Tags.SC.getTag(), replenishRequest.getTokenInfo().getHceData().getDynParams().getSc());
            List<String> tvlss = replenishRequest.getTvls();
            Log.d("ReplenishmentReq", tvlss == null ? "Getting TVL from visaPaymentSDK.constructReplenishRequest null" :
                    "Getting TVL from visaPaymentSDK.constructReplenishRequest size : " + tvlss.size());
            JSONArray jsArrTvl = new JSONArray();
            List<TvlEntry> tvls = visaPaymentSDK.getTvlLog(tokenKey);
            Log.d("ReplenishmentReq", tvlss == null ? "Getting TVL from visaPaymentSDK.getTvlLog null" :
                    "Getting TVL from visaPaymentSDK.getTvlLog size : " + tvlss.size());
            TvlEntry tvlEntry;
            for (int i = 0; i < tvls.size(); i++) {
                tvlEntry = tvls.get(i);
                jsArrTvl.put(i, tvlEntry.getTimeStamp() + "|" +
                        tvlEntry.getUnpredictableNumber() + "|" +
                        tvlEntry.getAtc() + "|" +
                        tvlEntry.getTransactionType());
            }
            jsReplenishReq.put("tvl", jsArrTvl);
        } catch (JSONException e) {
            listener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (TokenInvalidException e) {
            listener.onError(SdkErrorStandardImpl.SDK_INVALID_CARD_NUMBER);
            return;
        } catch (CryptoException cryptoException) {
            if (cryptoException.getCbpError().getErrorCode() == SDKErrorType.SUPER_USER_PERMISSION_DETECTED.getCode()) {
                ComvivaSdk.reportFraud();
                if (listener != null) {
                    listener.onError(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
                }
            } else {
                if (listener != null) {
                    listener.onError(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
                }
            }
        } catch (Exception e) {
            listener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            return;
        }
        class GetPanDataTask extends AsyncTask<Void, Void, HttpResponse> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }


            @Override
            protected HttpResponse doInBackground(Void... params) {

                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSReplenishTokenUrl(), jsReplenishReq.toString());
            }


            @Override
            protected void onPostExecute(HttpResponse httpResponse) {

                super.onPostExecute(httpResponse);
                try {
                    try {
                        ComvivaSdk.checkSecurity();
                    } catch (SdkException e) {
                        Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                        listener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
                        return;
                    }
                    if (httpResponse != null) {
                        if (httpResponse.getStatusCode() == 200) {
                            JSONObject jsReplenishResponse = new JSONObject(httpResponse.getResponse());
                            int statusCode = jsReplenishResponse.getInt(Tags.RESPONSE_CODE.getTag());
                            // Error
                            if (statusCode != 200) {
                                listener.onError(SdkErrorImpl.getInstance(statusCode, jsReplenishResponse.getString(Tags.MESSAGE.getTag())));
                                return;
                            }
                            Gson gson = new Gson();
                            final ReplenishResponse replenishResponse = gson.fromJson(jsReplenishResponse.toString(), ReplenishResponse.class);
                            final LukInfo lukInfo = new LukInfo();
                            lukInfo.setCard(paymentCard.getCardUniqueId());
                            DynParams dynParams = replenishResponse.getTokenInfo().getHceData().getDynParams();
                            lukInfo.setNoOfPaymentsRemaining(dynParams.getMaxPmts());
                            lukInfo.setKeyExpTime(new Date(dynParams.getKeyExpTS()));
                            boolean isReplenishSuccess = visaPaymentSDK.processReplenishmentResponse(tokenKey, replenishResponse.getTokenInfo());
                            if (isReplenishSuccess) {
                                ConfirmProvisionListener confirmProvisionListener = new ConfirmProvisionListener() {
                                    @Override
                                    public void onCompleted() {

                                        listener.onSuccess();
                                        try {
                                            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                                            comvivaSdk.updateLukInfo(lukInfo);
                                        } catch (SdkException e) {
                                            Log.d("ComvivaSdkError", e.getMessage());
                                        }
                                    }


                                    @Override
                                    public void onStarted() {

                                    }


                                    @Override
                                    public void onError(SdkError sdkError) {

                                        listener.onError(sdkError);
                                    }
                                };
                                ReplenishAckRequest replenishAckRequest = visaPaymentSDK.constructReplenishAcknowledgementRequest(tokenKey);
                                ConfirmReplenishTask(replenishAckRequest, tokenData.getVProvisionedTokenID(), confirmProvisionListener);
                            }
                        }
                    }
                } catch (JSONException e) {
                    listener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                } catch (RootDetectException rootDetection) {
                    if (listener != null) {
                        listener.onError(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
                    }
                } catch (Exception e) {
                    listener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        GetPanDataTask getPanDataTask = new GetPanDataTask();
        getPanDataTask.execute();
    }


    private void handleCardLCMSuccessResponse(CardLCMOperationResponse cardLcmOperationResponse, ResponseListener responseListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(cardLcmOperationResponse.getResponseCode())) {
            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            TokenKey tokenKey = ((TokenData) paymentCard.getCurrentCard()).getTokenKey();
            switch (cardLcmRequestParam.getCardLcmOperation()) {
                case DELETE:
                    try {
                        comvivaSdk = sdkData.getComvivaSdk();
                        visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.DELETED);
                        sdkData.getCardSelectionManagerForTransaction().unSetPaymentCardForTransaction();
                        // Manage default card if card being deleted is default one
                        ArrayList<PaymentCard> allCards = comvivaSdk.getAllCards();
                        int noOfCards = allCards.size();
                        if (noOfCards == 0) {
                            // There is no card remaining now
                            comvivaSdk.resetDefaultCard();
                        } else {
                            String defaultCardUniqueId = comvivaSdk.getDefaultCardUniqueId();
                            if (defaultCardUniqueId != null && paymentCard.getCardUniqueId().equalsIgnoreCase(defaultCardUniqueId)) {
                                comvivaSdk.resetDefaultCard();
                            }
                        }
                        //comvivaSdk.deleteLukInfo(card);
                    } catch (SdkException e) {
                        Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                        responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                        return;
                    } catch (Exception e) {
                        Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                        responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                        return;
                    }
                    responseListener.onSuccess();
                    break;
                case SUSPEND:
                    visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.SUSPENDED);
                    responseListener.onSuccess();
                    break;
                case RESUME:
                    visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.ACTIVE);
                    responseListener.onSuccess();
                    break;
            }
        } else {
            responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(cardLcmOperationResponse.getResponseCode()), cardLcmOperationResponse.getResponseMessage()));
        }
    }


    private void handleEnrollPanResponse(EnrollPanResponse enrollPanResponse, CheckCardEligibilityListener checkCardEligibilityListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(enrollPanResponse.getResponseCode())) {
            this.enrollPanResponse = enrollPanResponse;
            sdkData.setEnrollPanPerformed(true);
            try {
                networkApi.setServerAuthenticateListener(this);
                networkApi.getTermsAndConditionVts(enrollPanResponse.getCardMetaData().getTermsAndConditionsID(), checkCardEligibilityListener);
            } catch (SdkException e) {
                Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                checkCardEligibilityListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            }
        } else {
            checkCardEligibilityListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(enrollPanResponse.getResponseCode()), enrollPanResponse.getResponseMessage()));
        }
    }


    private void handleTermsAndConditionVtsResponse(GetTermsAndCondtionVtsResponse getTermsAndCondtionVtsResponse, Object listener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(getTermsAndCondtionVtsResponse.getResponseCode())) {
            ContentGuid contentGuid = parseGetContentResponse(getTermsAndCondtionVtsResponse);
            if (listener instanceof GetAssetListener) {
                GetAssetListener getAssetListener = (GetAssetListener) listener;
                getAssetListener.onCompleted(contentGuid);
            } else if (listener instanceof CheckCardEligibilityListener) {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onTermsAndConditionsRequired(contentGuid);
            }
        } else {
            if (listener instanceof CheckCardEligibilityListener) {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(getTermsAndCondtionVtsResponse.getResponseCode()), getTermsAndCondtionVtsResponse.getResponseMessage()));
            } else if (listener instanceof GetAssetListener) {
                GetAssetListener getAssetListener = (GetAssetListener) listener;
                getAssetListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(getTermsAndCondtionVtsResponse.getResponseCode()), getTermsAndCondtionVtsResponse.getResponseMessage()));
            }
        }
    }


    private void handleTokenUpdateResponse(TokenUpdateResponse tokenUpdateResponse, TokenDataUpdateListener tokenDataUpdateListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(tokenUpdateResponse.getResponseCode())) {
            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            visaPaymentSDK.updateTokenStatus(tokenData.getTokenKey(), TokenStatus.getTokenStatus(tokenUpdateResponse.getTokenInfo().getTokenStatus()));
            tokenDataUpdateListener.onSuccess(tokenUpdateResponse.getTokenInfo().getTokenStatus());
        } else {
            tokenDataUpdateListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(tokenUpdateResponse.getResponseCode()), tokenUpdateResponse.getResponseMessage()));
        }
    }


    private void handleProvisionResponse(ProvisionResponse provisionResponse, DigitizationListener digitizationListener) {

        CommonUtil.setSharedPreference(provisionResponse.getVProvisionedTokenID(), enrollPanResponse.getvPanEnrollmentID(), Tags.USER_DETAILS.getTag());
        String tokenStatus = provisionResponse.getTokenInfo().getTokenStatus();
        if (tokenStatus != null && tokenStatus.equalsIgnoreCase(Tags.INACTIVE.getTag())) {
            provisionResponse.getTokenInfo().setTokenStatus(String.valueOf(TokenStatus.OBSOLETE));
        }
        provisionResponse.setVProvisionedTokenID(provisionResponse.getVProvisionedTokenID());
        this.provisionResponse = provisionResponse;
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        TokenKey tokenKey = visaPaymentSDK.storeProvisionedToken(provisionResponse, enrollPanResponse.getvPanEnrollmentID());
        // Confirm Provisioning
        ProvisionAckRequest provisionAckRequest = visaPaymentSDK.constructProvisionAck(tokenKey);
        try {
            networkApi.setServerAuthenticateListener(this);
            networkApi.confirmProvisionVts(provisionAckRequest, provisionResponse.getVProvisionedTokenID(), digitizationListener);
        } catch (SdkException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    private void handleConfirmProvisionResponse(ConfirmProvisioningResponse confirmProvisionResponse, DigitizationListener digitizationListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(confirmProvisionResponse.getResponseCode())) {
            // Insert LUK Information of this token
            LukInfo lukInfo = new LukInfo();
            lukInfo.setCard(provisionResponse.getVProvisionedTokenID());
            DynParams dynParams = provisionResponse.getTokenInfo().getHceData().getDynParams();
            lukInfo.setNoOfPaymentsRemaining(dynParams.getMaxPmts());
            lukInfo.setKeyExpTime(new Date(dynParams.getKeyExpTS()));
            try {
                comvivaSdk = ComvivaSdk.getInstance(null);
                comvivaSdk.insertLukInfo(lukInfo);
                if (provisionResponse.getStepUpRequest().size() != 0) {
                    digitizationListener.onRequireAdditionalAuthentication(enrollPanResponse.getvPanEnrollmentID(), provisionResponse.getVProvisionedTokenID(), prepareStepUpResponseObject(provisionResponse.getStepUpRequest()), enrollPanResponse.getCardMetaData());
                } else {
                    digitizationListener.onApproved(enrollPanResponse.getvPanEnrollmentID(), enrollPanResponse.getCardMetaData());
                    ArrayList<PaymentCard> cardList = comvivaSdk.getAllCards();
                    if ((cardList != null) && (cardList.size() == 1) && (cardList.get(0).getCardState().equals(CardState.ACTIVE))) {
                        comvivaSdk.setDefaultCard(cardList.get(0));
                    }
                }
            } catch (SdkException e) {
                Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            } catch (Exception e) {
                Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            }
        } else {
            digitizationListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(confirmProvisionResponse.getResponseCode()), confirmProvisionResponse.getResponseMessage()));
        }
    }


    private List<StepUpRequest> prepareStepUpResponseObject(List<com.visa.cbp.external.common.StepUpRequest> stepUpRequestList) {
        // Map Step up request List from visa to comviva SdDK  stepUpList
        List<StepUpRequest> stepUpRequestsList = new ArrayList<>();
        for (com.visa.cbp.external.common.StepUpRequest stepUpRequestVisa : stepUpRequestList) {
            StepUpRequest stepUpRequest = new StepUpRequest(stepUpRequestVisa.getIdentifier(), stepUpRequestVisa.getMethod(), stepUpRequestVisa.getValue());
            stepUpRequestsList.add(stepUpRequest);
        }
        return stepUpRequestsList;
    }


    private void handleError(Object listener, String... message) {

        if (message.length > 0) {
            if (listener instanceof CheckCardEligibilityListener) {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            } else if (listener instanceof StepUpListener) {
                StepUpListener stepUpListener = (StepUpListener) listener;
                stepUpListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            } else if (listener instanceof ResponseListener) {
                ResponseListener responseListener = (ResponseListener) listener;
                responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            } else if (listener instanceof DigitizationListener) {
                DigitizationListener digitizationListener = (DigitizationListener) listener;
                digitizationListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            }
        } else {
            if (listener instanceof CheckCardEligibilityListener) {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            } else if (listener instanceof StepUpListener) {
                StepUpListener stepUpListener = (StepUpListener) listener;
                stepUpListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            } else if (listener instanceof ResponseListener) {
                ResponseListener responseListener = (ResponseListener) listener;
                responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            } else if (listener instanceof DigitizationListener) {
                DigitizationListener digitizationListener = (DigitizationListener) listener;
                digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            }
        }
    }
}
