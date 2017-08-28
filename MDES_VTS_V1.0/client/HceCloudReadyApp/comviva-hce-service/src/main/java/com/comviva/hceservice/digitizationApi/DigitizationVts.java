package com.comviva.hceservice.digitizationApi;

import android.os.AsyncTask;

import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.digitizationApi.asset.AssetType;
import com.comviva.hceservice.digitizationApi.asset.MediaContent;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.UrlUtil;
import com.visa.cbp.external.common.*;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.external.enp.ProvisionResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DigitizationVts {
    private EnrollPanResponse enrollPanResponse;

    class GetTnCAssetTask extends AsyncTask<Void, Void, HttpResponse> {
        private Map<String, String> queryMap;
        private GetAssetListener listener;

        public GetTnCAssetTask(String guid, GetAssetListener listener) {
            queryMap = new HashMap<>();
            queryMap.put("guid", guid);
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpUtil httpUtil = HttpUtil.getInstance();
            return httpUtil.getRequest(UrlUtil.getVTSContentUrl(), queryMap);
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
        contentGuid.setAltText(jsGetContentResponse.getString("altText"));
        contentGuid.setContentType(ContentType.valueOf(jsGetContentResponse.getString("contentType")));
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
                mediaContents[i].setAssetType(AssetType.valueOf(contentType));
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

    private EnrollPanResponse parseEnrollPanResponse(JSONObject jsEnrollPanResp) throws JSONException {
        EnrollPanResponse enrollPanResponse = new EnrollPanResponse();
        enrollPanResponse.setvPanEnrollmentID(jsEnrollPanResp.getString(Tags.PAN_ENROLLMENT_ID.getTag()));

        // Payment Instrument
        JSONObject jsPaymentInstrument = jsEnrollPanResp.getJSONObject("paymentInstrumentComviva");
        PaymentInstrumentComviva paymentInstrumentComviva = new PaymentInstrumentComviva();
        paymentInstrumentComviva.setLast4(jsPaymentInstrument.getString("last4"));
        paymentInstrumentComviva.setCvv2PrintedInd(Boolean.getBoolean(jsEnrollPanResp.getString("cvv2PrintedInd")));
        paymentInstrumentComviva.setExpDatePrintedInd(Boolean.getBoolean(jsEnrollPanResp.getString("expDatePrintedInd")));
        ExpirationDate expirationDate = new ExpirationDate();
        expirationDate.setMonth(jsPaymentInstrument.getString("month"));
        expirationDate.setYear(jsPaymentInstrument.getString("year"));
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
        cardMetaData.setContactEmail(jsCardMetaData.getString("contactEmail"));
        cardMetaData.setContactName(jsCardMetaData.getString("contactName"));
        cardMetaData.setContactNumber(jsCardMetaData.getString("contactNumber"));
        cardMetaData.setForegroundColor(jsCardMetaData.getString("foregroundColor"));
        cardMetaData.setContactWebsite(jsCardMetaData.getString("contactWebsite"));
        cardMetaData.setShortDescription(jsCardMetaData.getString("shortDescription"));
        cardMetaData.setLabelColor(jsCardMetaData.getString("labelColor"));
        cardMetaData.setTermsAndConditionsID(jsCardMetaData.getString("termsAndConditionsID"));

        enrollPanResponse.setCardMetaData(cardMetaData);
        return enrollPanResponse;
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
        paymentInstrument.setAccountStatus(Boolean.valueOf(jsPaymentInstrument.getString("accountStatus")).name());
        paymentInstrument.setIsTokenizable(Boolean.valueOf(jsPaymentInstrument.getString("isTokenizable")).name());
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
            aidInfo.setAsrpd(jsAidInfo.getString("asrpd"));
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
        JSONObject jsMsdData = jsStaticParams.getJSONObject("MsdData");
        MsdData msdData = new MsdData();
        msdData.setAip(jsMsdData.getString("aip"));
        msdData.setAfl(jsMsdData.getString("afl"));
        staticParams.setMsdData(msdData);

        JSONObject jsQVSDCData = jsStaticParams.getJSONObject("qVSDCData");
        QVSDCData qvsdcData = new QVSDCData();
        qvsdcData.setCtq(jsQVSDCData.getString("ctq"));
        qvsdcData.setCed(jsQVSDCData.getString("ced"));
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

        JSONObject jsTrack2DataNotDec = jsStaticParams.getJSONObject("track2DataNotDec");
        Track2DataNotDec track2DataNotDec = new Track2DataNotDec();
        track2DataNotDec.setSvcCode(jsTrack2DataNotDec.getString("svcCode"));
        track2DataNotDec.setPinVerField(jsTrack2DataNotDec.getString("pinVerField"));
        track2DataNotDec.setTrack2DiscData(jsTrack2DataNotDec.getString("track2DiscData"));
        staticParams.setTrack2DataNotDec(track2DataNotDec);
        hceData.setStaticParams(staticParams);
        tokenInfo.setHceData(hceData);

        provisionResponse.setTokenInfo(tokenInfo);

        // ODAdata
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

    /**
     * Enroll PAN with VTS.
     *
     * @param cardEligibilityRequest   Eligibility request
     * @param checkEligibilityListener Eligibility Response
     */
    public void enrollPanVts(final CardEligibilityRequest cardEligibilityRequest, final CheckCardEligibilityListener checkEligibilityListener) {
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
        } catch (Exception e) {
            checkEligibilityListener.onCheckEligibilityError("Error while preparing request");
            return;
        }

        class EnrollPanTask extends AsyncTask<Void, Void, HttpResponse> {
            protected void onPreExecute() {
                super.onPreExecute();
                if (checkEligibilityListener != null) {
                    checkEligibilityListener.onCheckEligibilityCompleted();
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
                                checkEligibilityListener.onCheckEligibilityError("Server Error");
                            }
                        };
                        GetTnCAssetTask getTnCAssetTask = new GetTnCAssetTask(enrollPanResponse.getCardMetaData().getTermsAndConditionsID(), getAssetListener);
                        getTnCAssetTask.execute();
                    } else {
                        if (checkEligibilityListener != null) {
                            checkEligibilityListener.onCheckEligibilityError(httpResponse.getReqStatus().toString());
                        }
                    }
                } catch (Exception e) {
                    if (checkEligibilityListener != null) {
                        checkEligibilityListener.onCheckEligibilityError("Wrong data from server");
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
    public void provisionToken(DigitizationRequest digitizationRequest, final DigitizationListener digitizationListener) {
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
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSProvisionTokenUrl(), provisionTokenRequestObject.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (digitizationListener != null) {
                    digitizationListener.onDigitizationCompleted();
                }
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        JSONObject jsProvisionResp = new JSONObject(httpResponse.getResponse());

                        // Parse Provision Response and
                        String vProvisionedTokenID = jsProvisionResp.getString("vProvisionedTokenID");
                        ProvisionResponse provisionResponse = parseProvisionResponse(jsProvisionResp);
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


}
