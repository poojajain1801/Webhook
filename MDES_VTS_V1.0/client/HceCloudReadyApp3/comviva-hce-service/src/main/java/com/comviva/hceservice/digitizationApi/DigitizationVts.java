package com.comviva.hceservice.digitizationApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.comviva.hceservice.LukInfo;
import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.digitizationApi.asset.AssetType;
import com.comviva.hceservice.digitizationApi.asset.MediaContent;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.comviva.hceservice.security.RSAUtil;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.ResponseListener;
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
import com.visa.cbp.external.common.StepUpRequest;
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
import com.visa.cbp.sdk.facade.exception.VisaPaymentSDKException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import flexjson.JSONDeserializer;


class DigitizationVts {
    private EnrollPanResponse enrollPanResponse;

    class GetTnCAssetTask extends AsyncTask<Void, Void, HttpResponse> {
        private JSONObject jsGetContentReq;
        private GetAssetListener listener;

        public GetTnCAssetTask(String guid, GetAssetListener listener) {
            jsGetContentReq = new JSONObject();
            try {
                jsGetContentReq.put("guid", guid);
            } catch (JSONException e) {
            }
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpUtil httpUtil = HttpUtil.getInstance();
            return httpUtil.postRequest(UrlUtil.getVTSContentUrl(), jsGetContentReq.toString());
        }

        @Override
        protected void onPostExecute(HttpResponse httpResponse) {
            super.onPostExecute(httpResponse);
            try {
                if (httpResponse.getStatusCode() == 200) {
                    JSONObject jsContentGuid = new JSONObject(httpResponse.getResponse());
                    ContentGuid contentGuid = parseGetContentResponse(jsContentGuid);
                    listener.onCompleted(contentGuid);
                } else {
                    listener.onError(httpResponse.getResponse());
                }
            } catch (JSONException e) {
                listener.onError("Wrong data from server");
            }catch(Exception e)
            {
                listener.onError("SDK Internal Error");
            }
        }
    }

    class ConfirmProvisionTask extends AsyncTask<Void, Void, HttpResponse> {
        private ConfirmProvisionListener listener;
        private JSONObject jsConfirmProvisionReq;

        public ConfirmProvisionTask(ProvisionAckRequest provisionAckRequest, String vProvisionedTokenID, ConfirmProvisionListener listener) {
            this.listener = listener;

            try {
                jsConfirmProvisionReq = new JSONObject();
                jsConfirmProvisionReq.put("vprovisionedTokenId", vProvisionedTokenID);
                jsConfirmProvisionReq.put("api", provisionAckRequest.getApi());
                jsConfirmProvisionReq.put("provisioningStatus", provisionAckRequest.getProvisioningStatus());
                String failureReason = provisionAckRequest.getFailureReason();
                if (failureReason != null && failureReason.equalsIgnoreCase("FAILURE")) {
                    jsConfirmProvisionReq.put("failureReason", "Processing Failure");
                }
            } catch (JSONException e) {
                if (listener != null) {
                    listener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
                }
            }catch(Exception e)
            {
                listener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpUtil httpUtil = HttpUtil.getInstance();
            return httpUtil.postRequest(UrlUtil.getVTSConfirmProvisioningUrl(), jsConfirmProvisionReq.toString());
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
            }catch (Exception e){
                listener.onError(SdkErrorStandardImpl.SERVER_NOT_RESPONDING);
            }
        }
    }

    class ConfirmReplenishTask extends AsyncTask<Void, Void, HttpResponse> {
        private ConfirmProvisionListener listener;
        private JSONObject jsConfirmReplenishment;

        public ConfirmReplenishTask(ReplenishAckRequest replenishAckRequest, String vProvisionedTokenID, ConfirmProvisionListener listener) {
            this.listener = listener;

            try {
                jsConfirmReplenishment = new JSONObject();
                jsConfirmReplenishment.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), vProvisionedTokenID);
                jsConfirmReplenishment.put(Tags.API.getTag(), replenishAckRequest.getTokenInfo().getHceData().getDynParams().getApi());
                jsConfirmReplenishment.put(Tags.SC.getTag(), replenishAckRequest.getTokenInfo().getHceData().getDynParams().getSc());
            } catch (JSONException e) {
                if (listener != null) {
                    listener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
                }
            }
        }

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
            }catch(Exception e)
            {
                listener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            }
        }
    }

    private ContentGuid parseGetContentResponse(JSONObject jsGetContentResponse) throws JSONException,Exception {
        ContentGuid contentGuid = new ContentGuid();
        if (jsGetContentResponse.has("altText")) {
            contentGuid.setAltText(jsGetContentResponse.getString("altText"));
        }
        contentGuid.setContentType(ContentType.getContentType(jsGetContentResponse.getString("contentType")));
        JSONArray jsArrContent = jsGetContentResponse.getJSONArray("content");
        MediaContent[] mediaContents = new MediaContent[jsArrContent.length()];
        JSONObject jsContent;
        for (int i = 0; i < jsArrContent.length(); i++) {
            jsContent = jsArrContent.getJSONObject(0);
            mediaContents[i] = new MediaContent();
            mediaContents[i].setData(jsContent.getString("encodedData"));
            String contentType = jsContent.getString("mimeType");
            if (contentType.equalsIgnoreCase("image/pdf")) {
                mediaContents[i].setAssetType(AssetType.APPLICATION_PDF);
            } else {
                mediaContents[i].setAssetType(AssetType.getType(contentType));
            }

            switch (mediaContents[i].getAssetType()) {
                case IMAGE_PNG:
                case APPLICATION_PDF:
                    mediaContents[i].setHeight(jsContent.getInt("height"));
                    mediaContents[i].setWidth(jsContent.getInt("width"));
            }
        }
        contentGuid.setContent(mediaContents);
        return contentGuid;
    }


   /* private ContentGuid parseGetContentResponse(JSONObject jsGetContentResponse) throws JSONException {
        ContentGuid contentGuid = new ContentGuid();
        if (jsGetContentResponse.has("altText")) {
            contentGuid.setAltText(jsGetContentResponse.getString("altText"));
        }
        contentGuid.setContentType(ContentType.getContentType(jsGetContentResponse.getString("contentType")));
        JSONArray jsArrContent = jsGetContentResponse.getJSONArray("content");
        MediaContent[] mediaContents = new MediaContent[jsArrContent.length()];
        JSONObject jsContent;
        for (int i = 0; i < jsArrContent.length(); i++) {
            jsContent = jsArrContent.getJSONObject(0);
            mediaContents[i] = new MediaContent();
            mediaContents[i].setData(jsContent.getString("encodedData"));
            String contentType = jsContent.getString("mimeType");
            if (contentType.equalsIgnoreCase("image/pdf")) {
                mediaContents[i].setAssetType(AssetType.APPLICATION_PDF);
            } else {
                mediaContents[i].setAssetType(AssetType.getType(contentType));
            }

            switch (mediaContents[i].getAssetType()) {
                case IMAGE_PNG:
                case APPLICATION_PDF:
                    mediaContents[i].setHeight(jsContent.getInt("height"));
                    mediaContents[i].setWidth(jsContent.getInt("width"));
            }
        }
        contentGuid.setContent(mediaContents);
        return contentGuid;
    }*/

    private TokenInfo parseGetTokenResponse(JSONObject jsGetTokenResponse, TokenKey tokenKey) throws JSONException,Exception {

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

    private EnrollPanResponse parseEnrollPanResponse(JSONObject jsEnrollPanResp) throws JSONException,Exception {
        EnrollPanResponse enrollPanResponse = new EnrollPanResponse();
        enrollPanResponse.setvPanEnrollmentID(jsEnrollPanResp.getString(Tags.VPAN_ENROLLMENT_ID.getTag()));

        // Payment Instrument
        JSONObject jsPaymentInstrument = jsEnrollPanResp.getJSONObject("paymentInstrument");
        PaymentInstrumentComviva paymentInstrumentComviva = new PaymentInstrumentComviva();
        paymentInstrumentComviva.setLast4(jsPaymentInstrument.getString("last4"));
        paymentInstrumentComviva.setCvv2PrintedInd(Boolean.getBoolean(jsPaymentInstrument.getString("cvv2PrintedInd")));
        paymentInstrumentComviva.setExpDatePrintedInd(Boolean.getBoolean(jsPaymentInstrument.getString("expDatePrintedInd")));
        ExpirationDate expirationDate = new ExpirationDate();
        JSONObject jsExpirationDate = jsPaymentInstrument.getJSONObject("expirationDate");
        expirationDate.setMonth(jsExpirationDate.getString("month"));
        expirationDate.setYear(jsExpirationDate.getString("year"));
        paymentInstrumentComviva.setExpirationDate(expirationDate);
        EnabledServices enabledServices = new EnabledServices();
        enabledServices.setMerchantPresentedQR(Boolean.getBoolean(jsPaymentInstrument.getJSONObject("enabledServices").getString("merchantPresentedQR")));
        paymentInstrumentComviva.setEnabledServices(enabledServices);
        enrollPanResponse.setPaymentInstrumentComviva(paymentInstrumentComviva);


        // Card MetaData
        JSONObject jsCardMetaData = jsEnrollPanResp.getJSONObject("cardMetaData");
        com.comviva.hceservice.digitizationApi.CardMetaData cardMetaData = new com.comviva.hceservice.digitizationApi.CardMetaData();
        if(jsCardMetaData.has("longDescription")) {
            cardMetaData.setLongDescription(jsCardMetaData.getString("longDescription"));
        }
        cardMetaData.setBackgroundColor(jsCardMetaData.getString("backgroundColor"));
        cardMetaData.setForegroundColor(jsCardMetaData.getString("foregroundColor"));
        cardMetaData.setShortDescription(jsCardMetaData.getString("shortDescription"));
        cardMetaData.setLabelColor(jsCardMetaData.getString("labelColor"));
        cardMetaData.setTermsAndConditionsID(jsCardMetaData.getString("termsAndConditionsID"));
        CardData[] cardDatas ;
        if(jsCardMetaData.has("cardData"))
        {
            JSONArray cardDataArray = jsCardMetaData.getJSONArray("cardData");
            cardDatas = new CardData[cardDataArray.length()];
            for(int i=0;i<cardDataArray.length();i++)
            {
                JSONObject cardDataObject =  cardDataArray.getJSONObject(i);
                CardData cardData = new CardData();
                cardData.setGuid(cardDataObject.getString("guid"));
                ContentType contentType = ContentType.getContentType(cardDataObject.getString("contentType"));
                cardData.setContentType(contentType);
                if(cardDataObject.has("content"))
                {
                    JSONArray contentArray = cardDataObject.getJSONArray("content");
                    Content content = new Content();
                    if(contentArray.getJSONObject(0).has("width"))
                    {
                        content.setWidth(Integer.parseInt(contentArray.getJSONObject(0).getString("width")));
                    }
                    if(contentArray.getJSONObject(0).has("height"))
                    {
                        content.setHeight(Integer.parseInt(contentArray.getJSONObject(0).getString("height")));
                    }
                    if(contentArray.getJSONObject(0).has("mimeType"))
                    {
                        content.setMimeType(AssetType.getType(contentArray.getJSONObject(0).getString("mimeType")));
                    }
                    cardData.setContent(content);
                }
                cardDatas[i] = cardData;
            }
            cardMetaData.setCardDatas(cardDatas);
        }


        if (jsCardMetaData.has("contactEmail")) {
            cardMetaData.setContactEmail(jsCardMetaData.getString("contactEmail"));
        }
        if (jsCardMetaData.has("contactName")) {
            cardMetaData.setContactName(jsCardMetaData.getString("contactName"));
        }
        if (jsCardMetaData.has("contactNumber")) {
            cardMetaData.setContactNumber(jsCardMetaData.getString("contactNumber"));
        }
        if (jsCardMetaData.has("contactWebsite")) {
            cardMetaData.setContactWebsite(jsCardMetaData.getString("contactWebsite"));
        }
        enrollPanResponse.setCardMetaData(cardMetaData);
        return enrollPanResponse;
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

    EnrollPanResponse getEnrollPanResponse() {
        return enrollPanResponse;
    }

    private void parseGetMetaDataResponse(JSONObject jsGetMetaDataResponse) throws JSONException,Exception {
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

    private void parseGetPanData(JSONObject jsGetPanDataResponse) throws JSONException,Exception {
        JSONObject jsPaymentInstrument = jsGetPanDataResponse.getJSONObject("paymentInstrument");

        String last4 = jsPaymentInstrument.getString("last4");
        String cvv2PrintedInd = jsPaymentInstrument.getString("cvv2PrintedInd");
        String expDatePrintedInd = jsPaymentInstrument.getString("expDatePrintedInd");
    }

    /**
     * Enroll PAN with VTS.
     *
     * @param cardEligibilityRequest   Eligibility request
     * @param checkEligibilityListener Eligibility Response
     */
    void enrollPanVts(final CardEligibilityRequest cardEligibilityRequest, final CheckCardEligibilityListener checkEligibilityListener) {
        final JSONObject jsonEnrollPanReq = new JSONObject();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            final String clientWalletAccId = comvivaSdk.getInitializationData().getClientWalletAccountId();

            JSONObject expirationDate = new JSONObject();
            expirationDate.put("month", cardEligibilityRequest.getExpiryMonth());
            expirationDate.put("year", cardEligibilityRequest.getExpiryYear());

            JSONObject encPaymentInstrument = new JSONObject();
            encPaymentInstrument.put("accountNumber", cardEligibilityRequest.getAccountNumber());
            String cvv2 = cardEligibilityRequest.getSecurityCode();
            if(cvv2 != null && !cvv2.isEmpty()) {
                encPaymentInstrument.put("cvv2", cardEligibilityRequest.getSecurityCode());
            }
            encPaymentInstrument.put("expirationDate", expirationDate);
            encPaymentInstrument.put("name", cardEligibilityRequest.getCardholderName());

            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();


            PropertyReader propertyReader = PropertyReader.getInstance(null);
            jsonEnrollPanReq.put("clientAppId", propertyReader.getProperty(PropertyConst.KEY_CLIENT_APP_ID));
            jsonEnrollPanReq.put("clientWalletAccountId", clientWalletAccId);
            jsonEnrollPanReq.put("clientDeviceID", visaPaymentSDK.getDeviceId());
            jsonEnrollPanReq.put("consumerEntryMode", cardEligibilityRequest.getConsumerEntryMode().name());
            jsonEnrollPanReq.put("encPaymentInstrument", encPaymentInstrument);
            jsonEnrollPanReq.put("locale", cardEligibilityRequest.getLocale());
            jsonEnrollPanReq.put("panSource", cardEligibilityRequest.getPanSource().name());
        }catch (RootDetectException e){
            ComvivaSdk.reportFraud();
            checkEligibilityListener.onError(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
            return;
        }catch (Exception e) {
            checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }

        class EnrollPanTask extends AsyncTask<Void, Void, HttpResponse> {
            protected void onPreExecute() {
                super.onPreExecute();
                if (checkEligibilityListener != null) {
                    checkEligibilityListener.onStarted();
                }
            }

            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                ComvivaSdk comvivaSdk = null;
                HttpResponse httpResponse = null;
                try {
                     comvivaSdk = ComvivaSdk.getInstance(null);
                    httpResponse =  httpUtil.postRequest(UrlUtil.getVTSEnrollPanUrl(), RSAUtil.doMeth(comvivaSdk.getApplicationContext(),jsonEnrollPanReq.toString()));

                } catch (SdkException e) {
                    e.printStackTrace();
                }
                return httpResponse;
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsEnrollPanResp = new JSONObject(httpResponse.getResponse());

                        try {
                            if (jsEnrollPanResp.getInt(Tags.RESPONSE_CODE.getTag()) != 200) {
                                checkEligibilityListener.onError(SdkErrorImpl.getInstance(jsEnrollPanResp.getInt(Tags.RESPONSE_CODE.getTag()),
                                        jsEnrollPanResp.getString(Tags.MESSAGE.getTag())));
                                return;
                            }
                        } catch (Exception e) {
                            checkEligibilityListener.onError(SdkErrorImpl.getInstance(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION.getErrorCode(),
                                    jsEnrollPanResp.getString(Tags.MESSAGE.getTag())));
                        }
                        enrollPanResponse = parseEnrollPanResponse(jsEnrollPanResp);


                        GetAssetListener getAssetListener = new GetAssetListener() {
                            @Override
                            public void onStarted() {
                            }

                            @Override
                            public void onCompleted(ContentGuid contentGuid) {
                                checkEligibilityListener.onTermsAndConditionsRequired(contentGuid);
                                checkEligibilityListener.getCardMetadataDetails(enrollPanResponse.getCardMetaData());

                            }

                            @Override
                            public void onError(String message) {
                                checkEligibilityListener.onError(SdkErrorImpl.getInstance(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR.getErrorCode(), message));
                            }
                        };
                        GetTnCAssetTask getTnCAssetTask = new GetTnCAssetTask(enrollPanResponse.getCardMetaData().getTermsAndConditionsID(), getAssetListener);
                        getTnCAssetTask.execute();
                    } else {
                        if (checkEligibilityListener != null) {
                            checkEligibilityListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                        }
                    }
                } catch (Exception e) {
                    if (checkEligibilityListener != null) {
                        checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
                    }
                }
            }
        }

        EnrollPanTask enrollPanTask = new EnrollPanTask();
        enrollPanTask.execute();
    }

    /**
     * Provision a given token previously enrolled.
     *
     * @param digitizationRequest  Digitization Request
     * @param digitizationListener UI listener for Digitization
     */
    void provisionToken(DigitizationRequest digitizationRequest, final DigitizationListener digitizationListener) {
        final JSONObject provisionTokenRequestObject = new JSONObject();
        final VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        try {
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            final String clientWalletAccId = comvivaSdk.getInitializationData().getClientWalletAccountId();

            PropertyReader propertyReader = PropertyReader.getInstance(null);
            provisionTokenRequestObject.put(Tags.CLIENT_APP_ID.getTag(), propertyReader.getProperty(PropertyConst.KEY_CLIENT_APP_ID));
            provisionTokenRequestObject.put(Tags.CLIENT_WALLET_ACCOUNT_ID.getTag(), clientWalletAccId);
            provisionTokenRequestObject.put(Tags.CLIENT_DEVICE_ID.getTag(), visaPaymentSDK.getDeviceId());
            provisionTokenRequestObject.put(Tags.PAN_ENROLLMENT_ID.getTag(), enrollPanResponse.getvPanEnrollmentID());
            provisionTokenRequestObject.put(Tags.TERMS_AND_CONDITION_ID.getTag(), enrollPanResponse.getCardMetaData().getTermsAndConditionsID());
            provisionTokenRequestObject.put(Tags.EMAIL_ADDRESS.getTag(), digitizationRequest.getEmailAddress());
            provisionTokenRequestObject.put(Tags.PROTECTION_TYPE.getTag(), Constants.PROTECTION_TYPE);
            provisionTokenRequestObject.put(Tags.PRESENTATION_TYPE.getTag(), Constants.PRESENTATION_TYPE);
        } catch (JSONException e) {
            digitizationListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }catch (RootDetectException e){
            ComvivaSdk.reportFraud();
            digitizationListener.onError(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
        }

        class ProvisionTokenTask extends AsyncTask<Void, Void, HttpResponse> {
            protected void onPreExecute() {
                super.onPreExecute();
                if (digitizationListener != null) {
                    digitizationListener.onStarted();
                }
            }

            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSProvisionTokenUrl(), provisionTokenRequestObject.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    try {
                        ComvivaSdk.checkSecurity();
                    } catch (SdkException e) {
                        e.printStackTrace();
                        digitizationListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
                        return;
                    }
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsProvisionResp = new JSONObject(httpResponse.getResponse());

                        if (jsProvisionResp.getInt(Tags.RESPONSE_CODE.getTag()) != 200) {
                            digitizationListener.onError(SdkErrorImpl.getInstance(jsProvisionResp.getInt(Tags.RESPONSE_CODE.getTag()),
                                    jsProvisionResp.getString(Tags.MESSAGE.getTag())));
                            return;
                        }

                        // Parse Provision Response and
                        final String vProvisionedTokenID = jsProvisionResp.getString("vProvisionedTokenID");
                        ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                        SharedPreferences pref = comvivaSdk.getApplicationContext().getSharedPreferences(Tags.VPAN_ENROLLMENT_ID.getTag(), Context.MODE_PRIVATE); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(vProvisionedTokenID,enrollPanResponse.getvPanEnrollmentID()); // Storing string
                        editor.commit();


                        Gson gson = new Gson();
                        final ProvisionResponse provisionResponse = gson.fromJson(jsProvisionResp.toString(), ProvisionResponse.class);
                        String tokenStatus = provisionResponse.getTokenInfo().getTokenStatus();
                        if(tokenStatus != null && tokenStatus.equalsIgnoreCase("INACTIVE")) {
                            provisionResponse.getTokenInfo().setTokenStatus(String.valueOf(TokenStatus.OBSOLETE));
                        }
                        provisionResponse.setVProvisionedTokenID(vProvisionedTokenID);
                        TokenKey tokenKey = visaPaymentSDK.storeProvisionedToken(provisionResponse, enrollPanResponse.getvPanEnrollmentID());

                        // Confirm Provisioning
                        ProvisionAckRequest provisionAckRequest = visaPaymentSDK.constructProvisionAck(tokenKey);
                        ConfirmProvisionTask confirmProvisionTask = new ConfirmProvisionTask(provisionAckRequest, vProvisionedTokenID,
                                new ConfirmProvisionListener() {
                                    @Override
                                    public void onStarted() {
                                    }

                                    @Override
                                    public void onCompleted() {
                                        try {
                                            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                                            // Insert LUK Information of this token
                                            LukInfo lukInfo = new LukInfo();
                                            lukInfo.setCard(provisionResponse.getVProvisionedTokenID());
                                            DynParams dynParams = provisionResponse.getTokenInfo().getHceData().getDynParams();
                                            lukInfo.setNoOfPaymentsRemaining(dynParams.getMaxPmts());
                                            lukInfo.setKeyExpTime(new Date(dynParams.getKeyExpTS()));
                                            comvivaSdk.insertLukInfo(lukInfo);


                                            ArrayList<PaymentCard> cardList = comvivaSdk.getAllCards();
                                            if (cardList != null && cardList.size() == 1) {
                                                comvivaSdk.setDefaultCard(cardList.get(0));
                                            }


                                        } catch (SdkException e) {
                                            Log.d("ComvivaSdkError", e.getMessage());
                                        }
                                        // TODO Step-up Options required
                                        if (provisionResponse.getStepUpRequest().size() !=0) {
                                            digitizationListener.onRequireAdditionalAuthentication(enrollPanResponse.getvPanEnrollmentID(), vProvisionedTokenID ,provisionResponse.getStepUpRequest());
                                        }else {
                                            digitizationListener.onApproved(enrollPanResponse.getvPanEnrollmentID());
                                        }
                                    }

                                    @Override
                                    public void onError(SdkError sdkError) {
                                        digitizationListener.onError(sdkError);
                                    }
                                });

                        confirmProvisionTask.execute();


                    } else {
                        if (digitizationListener != null) {
                            digitizationListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                        }
                    }
                } catch (JSONException e) {
                    if (digitizationListener != null) {
                        digitizationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } catch (CryptoException cryptoException) {
                    //cbpError.getErrorCode() == SDKErrorType.CVM_VERIFICATION_REQUIRED.getCode()
                        if (cryptoException.getCbpError().getErrorCode() == SDKErrorType.SUPER_USER_PERMISSION_DETECTED.getCode())
                        {
                            ComvivaSdk.reportFraud();
                            if (digitizationListener != null) {
                                digitizationListener.onError(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
                            }
                        }else
                        {
                            if (digitizationListener != null) {
                                digitizationListener.onError(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
                            }
                        }
                    if (digitizationListener != null) {
                        digitizationListener.onError(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
                    }
                } catch (Exception e) {
                    if (digitizationListener != null) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                    }
                }
            }
        }
        ProvisionTokenTask provisionTokenTask = new ProvisionTokenTask();
        provisionTokenTask.execute();
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
                                ComvivaSdk comvivaSdk =  ComvivaSdk.getInstance(null);
                                SharedPreferences pref = comvivaSdk.getApplicationContext().getSharedPreferences(Tags.VPAN_ENROLLMENT_ID.getTag(), comvivaSdk.getApplicationContext().MODE_PRIVATE);
                                 digitizationListener.onApproved( pref.getString(tokenData.getVProvisionedTokenID(), null));
                            }
                        }
                    } catch (JSONException e) {
                        if (digitizationListener != null) {
                            digitizationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                        }
                    } catch (SdkException e) {
                        e.printStackTrace();
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
    void getContent(final String guid, GetAssetListener getAssetListener) {
        GetTnCAssetTask getTnCAssetTask = new GetTnCAssetTask(guid, getAssetListener);
        getTnCAssetTask.execute();
    }


    /**
     * This API is used to Suspend, UnSuspend and Delete Token
     */
    void performCardLcm(final PaymentCard card,
                        final CardLcmOperation cardLcmOperation,
                        final CardLcmReasonCode reasonCode,
                        final CardLcmListener cardLcmListener) {

        final JSONObject jsCardLcmReq = new JSONObject();
        try {
            jsCardLcmReq.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), ((TokenData) card.getCurrentCard()).getVProvisionedTokenID());
            jsCardLcmReq.put("reasonCode", reasonCode.name());
            jsCardLcmReq.put("operation", cardLcmOperation.name());
        } catch (JSONException e) {
            cardLcmListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }
        catch (Exception e) {
            cardLcmListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
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
                return httpUtil.postRequest(UrlUtil.getCardLifeCycleManagementVtsUrl(cardLcmOperation), jsCardLcmReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try{
                if (httpResponse.getStatusCode() == 200) {
                    try {

                        // Get all tokens
                        JSONObject jsResponse = new JSONObject(httpResponse.getResponse());
                        if (jsResponse.has(Tags.RESPONSE_CODE.getTag()) && !jsResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            cardLcmListener.onError(SdkErrorImpl.getInstance(jsResponse.getInt(Tags.RESPONSE_CODE.getTag()), jsResponse.getString("message")));
                            return;
                        }

                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = ((TokenData) card.getCurrentCard()).getTokenKey();
                        if (jsResponse.has(Tags.RESPONSE_CODE.getTag()) && jsResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            switch (cardLcmOperation) {
                                case DELETE:
                                    try {
                                        ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                                        visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.DELETED);
                                        comvivaSdk.deSelectCard();

                                        // Manage default card if card being deleted is default one
                                        ArrayList<PaymentCard> allCards = comvivaSdk.getAllCards();
                                        int noOfCards = allCards.size();
                                        if (noOfCards == 0) {
                                            // There is no card remaining now
                                            comvivaSdk.resetDefaultCard();
                                        } else {
                                            String defaultCardUniqueId = comvivaSdk.getDefaultCardUniqueId();
                                            if (defaultCardUniqueId != null && card.getCardUniqueId().equalsIgnoreCase(defaultCardUniqueId)) {
                                                /*PaymentCard card1;
                                                switch (noOfCards) {
                                                    case 1:
                                                        // There is single card remaining make it default
                                                    default:
                                                        // There is more than one card remaining, first card card coming from list, will be default
                                                        comvivaSdk.setDefaultCard(allCards.get(0));
                                                        card1 = allCards.get(0);
                                                        String last4digit = card1.getCardLast4Digit();
                                                       // Show push message

                                                }*/
                                                comvivaSdk.resetDefaultCard();
                                            }
                                        }

                                        //comvivaSdk.deleteLukInfo(card);
                                    } catch (SdkException e) {
                                        Log.d("Error", e.getMessage());
                                    }
                                    cardLcmListener.onSuccess("Card will be Deleted Successfully");
                                    break;

                                case SUSPEND:
                                    visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.SUSPENDED);
                                    cardLcmListener.onSuccess("Card will be suspended successfully");
                                    break;

                                case RESUME:
                                    visaPaymentSDK.updateTokenStatus(tokenKey, TokenStatus.RESUME);
                                    cardLcmListener.onSuccess("Card will be resumed successfully");
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        cardLcmListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } else {
                    cardLcmListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }
            }catch (Exception e) {
                    cardLcmListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        CardLcmTask cardLcmTask = new CardLcmTask();
        cardLcmTask.execute();
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
                }catch (Exception e)
                {
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
                }catch(Exception e){
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
            jsReplenishReq.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), tokenData.getVProvisionedTokenID());
            jsReplenishReq.put(Tags.MAC.getTag(), replenishRequest.getSignature().getMac());
            jsReplenishReq.put(Tags.API.getTag(), replenishRequest.getTokenInfo().getHceData().getDynParams().getApi());
            jsReplenishReq.put(Tags.SC.getTag(), replenishRequest.getTokenInfo().getHceData().getDynParams().getSc());
            List<String> tvlss =  replenishRequest.getTvls();
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
        }catch (Exception e) {
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
                        e.printStackTrace();
                        listener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
                        return;
                    }

                    if(httpResponse!=null) {
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
                                ConfirmReplenishTask confirmReplenishTask = new ConfirmReplenishTask(replenishAckRequest,
                                        tokenData.getVProvisionedTokenID(), confirmProvisionListener);
                                confirmReplenishTask.execute();
                            }
                        }
                    }
                } catch (JSONException e) {

                    listener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                }catch (RootDetectException rootDetection) {
                    if (listener != null) {
                        listener.onError(SdkErrorStandardImpl.COMMON_DEVICE_ROOTED);
                    }
                }catch (Exception e) {

                    listener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        GetPanDataTask getPanDataTask = new GetPanDataTask();
        getPanDataTask.execute();
    }

}
