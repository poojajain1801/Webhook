package com.comviva.hceservice.digitizationApi;

import android.os.AsyncTask;

import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.digitizationApi.asset.AssetType;
import com.comviva.hceservice.digitizationApi.asset.MediaContent;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.visa.cbp.external.common.AidInfo;
import com.visa.cbp.external.common.DynParams;
import com.visa.cbp.external.common.ExpirationDate;
import com.visa.cbp.external.common.HceData;
import com.visa.cbp.external.common.IccPubKeyCert;
import com.visa.cbp.external.common.MsdData;
import com.visa.cbp.external.common.ODAData;
import com.visa.cbp.external.common.PaymentInstrument;
import com.visa.cbp.external.common.QVSDCData;
import com.visa.cbp.external.common.QVSDCWithoutODA;
import com.visa.cbp.external.common.ReplenishODAData;
import com.visa.cbp.external.common.ReplenishODAResponse;
import com.visa.cbp.external.common.StaticParams;
import com.visa.cbp.external.common.StepUpRequest;
import com.visa.cbp.external.common.TokenBinPubKeyCert;
import com.visa.cbp.external.common.TokenInfo;
import com.visa.cbp.external.common.Track2DataDec;
import com.visa.cbp.external.common.Track2DataNotDec;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.external.enp.ProvisionResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.data.TokenStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                jsConfirmProvisionReq.put("vProvisionedTokenID", vProvisionedTokenID);
                jsConfirmProvisionReq.put("api", provisionAckRequest.getApi());
                jsConfirmProvisionReq.put("provisioningStatus", provisionAckRequest.getProvisioningStatus());
                if (provisionAckRequest.getFailureReason().equalsIgnoreCase("FAILURE")) {
                    jsConfirmProvisionReq.put("failureReason", "Processing Failure");
                }
                jsConfirmProvisionReq.put("reperso", "false");
            } catch (JSONException e) {
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

            if (httpResponse.getStatusCode() == 200) {
                listener.onCompleted();
            } else {
                listener.onError(httpResponse.getResponse());
            }
        }
    }

    private ContentGuid parseGetContentResponse(JSONObject jsGetContentResponse) throws JSONException {
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

    private EnrollPanResponse parseEnrollPanResponse(JSONObject jsEnrollPanResp) throws JSONException {
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
        cardMetaData.setLongDescription(jsCardMetaData.getString("longDescription"));
        cardMetaData.setBackgroundColor(jsCardMetaData.getString("backgroundColor"));
        cardMetaData.setForegroundColor(jsCardMetaData.getString("foregroundColor"));
        cardMetaData.setShortDescription(jsCardMetaData.getString("shortDescription"));
        cardMetaData.setLabelColor(jsCardMetaData.getString("labelColor"));
        cardMetaData.setTermsAndConditionsID(jsCardMetaData.getString("termsAndConditionsID"));

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

    private ProvisionResponse parseProvisionResponse(JSONObject jsProvisionResponse) throws JSONException {
        ProvisionResponse provisionResponse = new ProvisionResponse();

        // Payment Instrument
        JSONObject jsPaymentInstrument = jsProvisionResponse.getJSONObject("paymentInstrument");
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setLast4(jsPaymentInstrument.getString("last4"));
        JSONObject jsExpirationDate = jsPaymentInstrument.getJSONObject("expirationDate");
        ExpirationDate expirationDate = new ExpirationDate();
        expirationDate.setMonth(jsExpirationDate.getString("month"));
        expirationDate.setYear(jsExpirationDate.getString("year"));
        paymentInstrument.setExpirationDate(expirationDate);
        if (jsPaymentInstrument.has("accountStatus")) {
            paymentInstrument.setAccountStatus(Boolean.getBoolean(jsPaymentInstrument.getString("accountStatus")).name());
        }
        if (jsPaymentInstrument.has("isTokenizable")) {
            paymentInstrument.setIsTokenizable(Boolean.getBoolean(jsPaymentInstrument.getString("isTokenizable")).name());
        }
        provisionResponse.setPaymentInstrument(paymentInstrument);

        // TokenInfo
        JSONObject jsTokenInfo = jsProvisionResponse.getJSONObject("tokenInfo");
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setTokenStatus(jsTokenInfo.getString("tokenStatus"));
        tokenInfo.setTokenRequestorID(jsTokenInfo.getString("tokenRequestorID"));
        tokenInfo.setTokenReferenceID(jsTokenInfo.getString("tokenReferenceID"));
        tokenInfo.setLast4(jsTokenInfo.getString("last4"));
        JSONObject jsExpirationDateTi = jsPaymentInstrument.getJSONObject("expirationDate");
        ExpirationDate expirationDateTi = new ExpirationDate();
        expirationDateTi.setMonth(jsExpirationDateTi.getString("month"));
        expirationDateTi.setYear(jsExpirationDateTi.getString("year"));
        tokenInfo.setExpirationDate(expirationDateTi);
        tokenInfo.setAppPrgrmID(jsTokenInfo.getString("appPrgrmID"));
        tokenInfo.setEncTokenInfo(jsTokenInfo.getString("encTokenInfo"));
        JSONObject jsHceData = jsTokenInfo.getJSONObject("hceData");
        HceData hceData = new HceData();

        JSONObject jsDynParams = jsHceData.getJSONObject("dynParams");
        DynParams dynParams = new DynParams();
        dynParams.setEncKeyInfo(jsDynParams.getString("encKeyInfo"));
        dynParams.setMaxPmts(jsDynParams.getInt("maxPmts"));
        dynParams.setApi(jsDynParams.getString("api"));
        dynParams.setSc(jsDynParams.getInt("sc"));
        dynParams.setKeyExpTS(jsDynParams.getLong("keyExpTS"));
        dynParams.setDki(jsDynParams.getString("dki"));
        hceData.setDynParams(dynParams);

        JSONObject jsStaticParams = jsHceData.getJSONObject("staticParams");
        StaticParams staticParams = new StaticParams();
        JSONArray jsArrAidInfo = jsStaticParams.getJSONArray("aidInfo");
        List<AidInfo> lsAidInfo = new ArrayList<>();
        JSONObject jsAidInfo;
        AidInfo aidInfo;
        for (int i = 0; i < jsArrAidInfo.length(); i++) {
            jsAidInfo = jsArrAidInfo.getJSONObject(i);
            aidInfo = new AidInfo();
            aidInfo.setApplicationLabel(jsAidInfo.getString("applicationLabel"));
            aidInfo.setAid(jsAidInfo.getString("aid"));
            aidInfo.setPriority(jsAidInfo.getString("priority"));
            aidInfo.setCVMrequired(jsAidInfo.getString("CVMrequired"));
            aidInfo.setCap(jsAidInfo.getString("cap"));
            if (jsAidInfo.has("asrpd")) {
                aidInfo.setAsrpd(jsAidInfo.getString("asrpd"));
            }
            lsAidInfo.add(aidInfo);
        }
        staticParams.setAidInfo(lsAidInfo);
        staticParams.setKernelIdentifier(jsStaticParams.getString("kernelIdentifier"));
        staticParams.setCardHolderNameVCPCS(jsStaticParams.getString("cardHolderNameVCPCS"));
        staticParams.setPdol(jsStaticParams.getString("pdol"));
        if (jsStaticParams.has("countrycode5F55")) {
            staticParams.setCountrycode5F55(jsStaticParams.getString("countrycode5F55"));
        }
        if (jsStaticParams.has("issuerIdentificationNumber")) {
            staticParams.setIssuerIdentificationNumber("issuerIdentificationNumber");
        }
        JSONObject jsMsdData = jsStaticParams.getJSONObject("msdData");
        MsdData msdData = new MsdData();
        msdData.setAip(jsMsdData.getString("aip"));
        msdData.setAfl(jsMsdData.getString("afl"));
        staticParams.setMsdData(msdData);

        JSONObject jsQVSDCData = jsStaticParams.getJSONObject("qVSDCData");
        QVSDCData qvsdcData = new QVSDCData();
        qvsdcData.setCtq(jsQVSDCData.getString("ctq"));
        if (jsQVSDCData.has("ced")) {
            qvsdcData.setCed(jsQVSDCData.getString("ced"));
        }
        qvsdcData.setFfi(jsQVSDCData.getString("ffi"));
        qvsdcData.setAuc(jsQVSDCData.getString("auc"));
        qvsdcData.setPsn(jsQVSDCData.getString("psn"));
        qvsdcData.setCvn(jsQVSDCData.getString("cvn"));
        qvsdcData.setDigitalWalletID(jsQVSDCData.getString("digitalWalletID"));
        qvsdcData.setCountryCode(jsQVSDCData.getString("countryCode"));
        qvsdcData.setCid(jsQVSDCData.getString("cid"));
        JSONObject jsQVSDCWithoutODA = jsQVSDCData.getJSONObject("qVSDCWithoutODA");
        QVSDCWithoutODA qvsdcWithoutODA = new QVSDCWithoutODA();
        qvsdcWithoutODA.setAip(jsQVSDCWithoutODA.getString("aip"));
        qvsdcWithoutODA.setAfl(jsQVSDCWithoutODA.getString("afl"));
        qvsdcData.setQVSDCWithoutODA(qvsdcWithoutODA);
        staticParams.setQVSDCData(qvsdcData);

        JSONObject jsTrack2DataDec = jsStaticParams.getJSONObject("track2DataDec");
        Track2DataDec track2DataDec = new Track2DataDec();
        track2DataDec.setSvcCode(jsTrack2DataDec.getString("svcCode"));
        staticParams.setTrack2DataDec(track2DataDec);

        if (jsStaticParams.has("track2DataNotDec")) {
            JSONObject jsTrack2DataNotDec = jsStaticParams.getJSONObject("track2DataNotDec");
            Track2DataNotDec track2DataNotDec = new Track2DataNotDec();
            track2DataNotDec.setSvcCode(jsTrack2DataNotDec.getString("svcCode"));
            track2DataNotDec.setPinVerField(jsTrack2DataNotDec.getString("pinVerField"));
            track2DataNotDec.setTrack2DiscData(jsTrack2DataNotDec.getString("track2DiscData"));
            staticParams.setTrack2DataNotDec(track2DataNotDec);
        }

        hceData.setStaticParams(staticParams);
        tokenInfo.setHceData(hceData);

        provisionResponse.setTokenInfo(tokenInfo);

        // ODAdata
        if (jsProvisionResponse.has("ODAdata")) {
            JSONObject jsODAdata = jsProvisionResponse.getJSONObject("ODAdata");
            ODAData odaData = new ODAData();
            odaData.setCaPubKeyIndex(jsODAdata.getString("caPubKeyIndex"));

            odaData.setAppFileLocator(jsODAdata.getString("appFileLocator"));
            odaData.setAppProfile(jsODAdata.getString("appProfile"));
            odaData.setEnciccPrivateKey(jsODAdata.getString("enciccPrivateKey"));

            JSONObject jsIccPubKeyCert = jsODAdata.getJSONObject("iccPubKeyCert");
            IccPubKeyCert iccPubKeyCert = new IccPubKeyCert();
            iccPubKeyCert.setCertificate(jsIccPubKeyCert.getString("certificate"));
            iccPubKeyCert.setExponent(jsIccPubKeyCert.getString("exponent"));
            if (jsIccPubKeyCert.has("remainder")) {
                iccPubKeyCert.setRemainder(jsIccPubKeyCert.getString("remainder"));
            }
            JSONObject jsExpirationDateOda = jsIccPubKeyCert.getJSONObject("expirationDate");
            ExpirationDate expirationDateOda = new ExpirationDate();
            expirationDate.setMonth(jsExpirationDateOda.getString("month"));
            expirationDate.setYear(jsExpirationDateOda.getString("year"));
            iccPubKeyCert.setExpirationDate(expirationDateOda);
            odaData.setIccPubKeyCert(iccPubKeyCert);

            TokenBinPubKeyCert tokenBinPubKeyCert = new TokenBinPubKeyCert();
            odaData.setTokenBinPubKeyCert(tokenBinPubKeyCert);

            provisionResponse.setODAData(odaData);
        }

        // TODO Find how to parse
        // Step Up Request
        if (jsProvisionResponse.has("stepUpRequest")) {
            JSONObject jsStepUpRequest = jsProvisionResponse.getJSONObject("stepUpRequest");
            int noOfStepupMethods = jsStepUpRequest.length();
            StepUpRequest stepUpRequest = new StepUpRequest(null);

            StepUpRequest.CREATOR.newArray(noOfStepupMethods);

            ArrayList<StepUpRequest> listStepUpRequests = new ArrayList<>();
            provisionResponse.setStepUpRequest(listStepUpRequests);
        }

        return provisionResponse;
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

    private void parseGetPanData(JSONObject jsGetPanDataResponse) throws JSONException {
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
            JSONObject expirationDate = new JSONObject();
            expirationDate.put("month", cardEligibilityRequest.getExpiryMonth());
            expirationDate.put("year", cardEligibilityRequest.getExpiryYear());

            JSONObject encPaymentInstrument = new JSONObject();
            encPaymentInstrument.put("accountNumber", cardEligibilityRequest.getAccountNumber());
            encPaymentInstrument.put("cvv2", cardEligibilityRequest.getSecurityCode());
            encPaymentInstrument.put("expirationDate", expirationDate);
            encPaymentInstrument.put("name", cardEligibilityRequest.getCardholderName());

            VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();

            PropertyReader propertyReader = PropertyReader.getInstance(null);
            jsonEnrollPanReq.put("clientAppId", propertyReader.getProperty(PropertyConst.KEY_CLIENT_APP_ID));
            jsonEnrollPanReq.put("clientWalletAccountId", propertyReader.getProperty(PropertyConst.KEY_CLIENT_WALLET_ACCOUNT_ID));
            jsonEnrollPanReq.put("clientDeviceID", visaPaymentSDK.getDeviceId());
            jsonEnrollPanReq.put("consumerEntryMode", cardEligibilityRequest.getConsumerEntryMode().name());
            jsonEnrollPanReq.put("encPaymentInstrument", encPaymentInstrument);
            jsonEnrollPanReq.put("locale", cardEligibilityRequest.getLocale());
            jsonEnrollPanReq.put("panSource", cardEligibilityRequest.getPanSource().name());

            jsonEnrollPanReq.put("userId", cardEligibilityRequest.getUserId());
        } catch (Exception e) {
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
                return httpUtil.postRequest(UrlUtil.getVTSEnrollPanUrl(), jsonEnrollPanReq.toString());
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
            PropertyReader propertyReader = PropertyReader.getInstance(null);
            provisionTokenRequestObject.put(Tags.CLIENT_APP_ID.getTag(), propertyReader.getProperty(PropertyConst.KEY_CLIENT_APP_ID));
            provisionTokenRequestObject.put(Tags.CLIENT_WALLET_ACCOUNT_ID.getTag(), propertyReader.getProperty(PropertyConst.KEY_CLIENT_WALLET_ACCOUNT_ID));
            provisionTokenRequestObject.put(Tags.CLIENT_DEVICE_ID.getTag(), visaPaymentSDK.getDeviceId());
            provisionTokenRequestObject.put(Tags.PAN_ENROLLMENT_ID.getTag(), enrollPanResponse.getvPanEnrollmentID());
            provisionTokenRequestObject.put(Tags.TERMS_AND_CONDITION_ID.getTag(), enrollPanResponse.getCardMetaData().getTermsAndConditionsID());
            provisionTokenRequestObject.put(Tags.EMAIL_ADDRESS.getTag(), digitizationRequest.getEmailAddress());
            provisionTokenRequestObject.put(Tags.PROTECTION_TYPE.getTag(), Constants.PROTECTION_TYPE);
            provisionTokenRequestObject.put(Tags.PRESENTATION_TYPE.getTag(), Constants.PRESENTATION_TYPE);
        } catch (JSONException e) {
            digitizationListener.onError("SDK Error : JSONException");
            return;
        }

        class ProvisionTokenTask extends AsyncTask<Void, Void, HttpResponse> {
            protected void onPreExecute() {
                super.onPreExecute();
                if (digitizationListener != null) {
                    digitizationListener.onDigitizationStarted();
                }
            }

            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSProvisionTokenUrl(), provisionTokenRequestObject.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsProvisionResp = new JSONObject(httpResponse.getResponse());

                        // Parse Provision Response and
                        String vProvisionedTokenID = jsProvisionResp.getString("vProvisionedTokenID");
                        ProvisionResponse provisionResponse = parseProvisionResponse(jsProvisionResp);
                        provisionResponse.setVProvisionedTokenID(vProvisionedTokenID);
                        TokenKey tokenKey = visaPaymentSDK.storeProvisionedToken(provisionResponse, enrollPanResponse.getvPanEnrollmentID());

                        // Confirm Provisioning
                        ProvisionAckRequest provisionAckRequest = visaPaymentSDK.constructProvisionAck(tokenKey);
                        ConfirmProvisionTask confirmProvisionTask = new ConfirmProvisionTask(provisionAckRequest, vProvisionedTokenID,
                                new ConfirmProvisionListener() {
                                    @Override
                                    public void onCompleted() {
                                        digitizationListener.onApproved();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        digitizationListener.onError(errorMessage);
                                    }
                                });

                        confirmProvisionTask.execute();

                        // TODO Step-up Options required
                        if (provisionResponse.getStepUpRequest() != null) {
                            digitizationListener.onRequireAdditionalAuthentication(null, null);
                        }
                    } else {
                        if (digitizationListener != null) {
                            digitizationListener.onError(httpResponse.getResponse());
                        }
                    }
                } catch (Exception e) {
                    if (digitizationListener != null) {
                        digitizationListener.onError("Wrong data from server");
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
     * @param tokenKey             TokenKey of which ODA data is to update
     * @param digitizationListener Listener
     */
    void replenishODADataRequest(final TokenKey tokenKey, final DigitizationListener digitizationListener) {
        final JSONObject replenishODADataObject = new JSONObject();
        try {
            // replenishODADataObject.put(Tags.USER_ID.getTag(), );
            replenishODADataObject.put(Tags.ACTIVATION_CODE.getTag(), "Dummy");
        } catch (JSONException e) {
            digitizationListener.onError("SDK Error : JSONException");
        }

        class ReplenishODADataTask extends AsyncTask<Void, Void, HttpResponse> {
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSReplenishODADataTokenUrl(), replenishODADataObject.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                try {
                    if (httpResponse.getStatusCode() == 200) {
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        JSONObject replenishODADataResponse = new JSONObject(httpResponse.getResponse());
                        visaPaymentSDK.processODAReplenishResponse(tokenKey, parseReplenishODADataResponse(replenishODADataResponse));
                        if (digitizationListener != null) {
                            digitizationListener.onApproved();
                        }
                    } else {
                        if (digitizationListener != null) {
                            digitizationListener.onError(httpResponse.getResponse());
                        }
                    }
                } catch (Exception e) {
                    if (digitizationListener != null) {
                        digitizationListener.onError("Wrong data from server");
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
    public void getContent(final String guid, GetAssetListener getAssetListener) {
        GetTnCAssetTask getTnCAssetTask = new GetTnCAssetTask(guid, getAssetListener);
        getTnCAssetTask.execute();
    }

    /**
     * Checks token's current status and update accordingly.
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
                return httpUtil.postRequest(UrlUtil.getVTSProvisionTokenUrl(), jsonTokenStatusRequest.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsGetTokenResponse = new JSONObject(httpResponse.getResponse());
                        parseGetTokenResponse(jsGetTokenResponse, tokenData.getTokenKey());
                        if (responseListener != null) {
                            responseListener.onSuccess();
                        }

                    } else if (responseListener != null) {
                        responseListener.onError(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                    if (responseListener != null) {
                        responseListener.onError(e.getMessage());
                    }
                }
            }
        }
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
            jsCardLcmReq.put("vProvisionedTokenID", ((TokenData) card.getCurrentCard()).getVProvisionedTokenID());
            jsCardLcmReq.put("reasonCode", reasonCode.name());
            jsCardLcmReq.put("operation", cardLcmOperation.name());
        } catch (JSONException e) {
            cardLcmListener.onError("SDK Exception:JSON Error");
            return;
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
                return httpUtil.postRequest(UrlUtil.getCardLifeCycleManagementVtsUrl(cardLcmOperation), jsCardLcmReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (httpResponse.getStatusCode() == 200) {
                    try {
                        // Get all tokens
                        JSONObject jsResponse = new JSONObject(httpResponse.getResponse());
                        if (jsResponse.has("reasonCode") && !jsResponse.getString("reasonCode").equalsIgnoreCase("200")) {
                            cardLcmListener.onError(jsResponse.getString("message"));
                            return;
                        }

                        if (jsResponse.has("reasonCode") && !jsResponse.getString("reasonCode").equalsIgnoreCase("200")) {
                            switch (cardLcmOperation) {
                                case DELETE:
                                    cardLcmListener.onSuccess("Card will be Deleted Successfully");
                                    break;

                                case SUSPEND:
                                    cardLcmListener.onSuccess("Card will be suspended successfully");
                                    break;

                                case RESUME:
                                    cardLcmListener.onSuccess("Card will be resumed successfully");
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        cardLcmListener.onError("Wrong data from server");
                    }
                } else {
                    cardLcmListener.onError(httpResponse.getResponse());
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
        } catch (Exception e) {
            responseListener.onError("SDK Error : JSONException");
            //checkEligibilityListener.onCheckEligibilityError("Error while preparing request");
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
                    } else if (responseListener != null) {
                        responseListener.onError(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                    if (responseListener != null) {
                        responseListener.onError(e.getMessage());
                    }
                    // listener.onError("Wrong data from server");
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
            responseListener.onError("SDK Error : JSONException");
            //checkEligibilityListener.onCheckEligibilityError("Error while preparing request");
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
                        responseListener.onError(httpResponse.getResponse());
                    }
                } catch (JSONException e) {
                    if (responseListener != null) {
                        responseListener.onError(e.getMessage());
                    }
                    // listener.onError("Wrong data from server");
                }
            }
        }
        GetPanDataTask getPanDataTask = new GetPanDataTask();
        getPanDataTask.execute();
    }

}
