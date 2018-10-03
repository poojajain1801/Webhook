package com.comviva.hceservice.digitizationApi;

import android.util.Log;

import com.comviva.hceservice.apiCalls.NetworkApi;
import com.comviva.hceservice.common.CardState;
import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.listeners.CheckCardEligibilityListener;
import com.comviva.hceservice.listeners.DigitizationListener;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.listeners.StepUpListener;
import com.comviva.hceservice.pojo.CardLCMOperationResponse;
import com.comviva.hceservice.pojo.GenerateOTPResponse;
import com.comviva.hceservice.pojo.GetAssetResponse;
import com.comviva.hceservice.pojo.VerifyOTPResponse;
import com.comviva.hceservice.pojo.digitizeMdes.DigitizeMdesResponse;
import com.comviva.hceservice.pojo.checkcardeligibility.CheckCardEligibilityResponse;
import com.comviva.hceservice.pojo.transactionhistorymdes.TransactionHistoryRegisterMdesResponse;
import com.comviva.hceservice.requestobjects.CardEligibilityRequestParam;
import com.comviva.hceservice.requestobjects.CardLcmRequestParam;
import com.comviva.hceservice.requestobjects.DigitizationRequestParam;
import com.comviva.hceservice.responseobject.StepUpRequest;
import com.comviva.hceservice.responseobject.authenticationmethods.AuthenticationMethod;
import com.comviva.hceservice.responseobject.contentguid.AssetType;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;
import com.comviva.hceservice.responseobject.contentguid.MediaContent;
import com.comviva.hceservice.util.Constants;
import com.mastercard.mpsdk.componentinterface.RolloverInProgressException;

import java.util.ArrayList;
import java.util.List;

class DigitizationMdes implements ServerResponseListener {

    private CheckCardEligibilityResponse checkCardEligibilityResponse;
    private SDKData sdkData;
    private NetworkApi networkApi;
    private CardLcmRequestParam cardLcmRequestParam;
    private PaymentCard paymentCard;
    private String tokenRef; // used for testing. to be removed


    public DigitizationMdes() {

        networkApi = new NetworkApi();
        sdkData = SDKData.getInstance();
    }


    @Override
    public void onRequestCompleted(Object result, Object listener) {

        try {
            if (result instanceof CheckCardEligibilityResponse) {
                CheckCardEligibilityResponse checkCardEligibilityResponse = (CheckCardEligibilityResponse) result;
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                handleCheckCardEligibilityResponse(checkCardEligibilityResponse, checkCardEligibilityListener);
            } else if (result instanceof GetAssetResponse) {
                GetAssetResponse getAssetResponse = (GetAssetResponse) result;
                handleGetAssetResponse(getAssetResponse, listener);
            } else if (result instanceof DigitizeMdesResponse) {
                DigitizeMdesResponse digitizeMdesResponse = (DigitizeMdesResponse) result;
                DigitizationListener digitizationListener = (DigitizationListener) listener;
                handleDigitizeMdesResponse(digitizeMdesResponse, digitizationListener);
            } else if (result instanceof GenerateOTPResponse) {
                GenerateOTPResponse generateOTPResponse = (GenerateOTPResponse) result;
                ResponseListener responseListener = (ResponseListener) listener;
                if (Constants.HTTP_RESPONSE_CODE_200.equals(generateOTPResponse.getResponseCode())) {
                    responseListener.onSuccess();
                } else {
                    responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(generateOTPResponse.getResponseCode()), generateOTPResponse.getResponseMessage()));
                }
            } else if (result instanceof VerifyOTPResponse) {
                VerifyOTPResponse verifyOTPResponse = (VerifyOTPResponse) result;
                ResponseListener responseListener = (ResponseListener) listener;
                if (Constants.HTTP_RESPONSE_CODE_200.equals(verifyOTPResponse.getResponseCode())) {
                    responseListener.onSuccess();
                  //  sdkData.getMcbp().getCardManager().activateCard(sdkData.getMcbp().getCardManager().getCardById(tokenRef));
                  //  sdkData.getMcbp().getCardManager().getCardById(tokenRef).replenishCredentials();
                  //  callRegisterForTransactionHistoryMdes();
                /*    ArrayList<PaymentCard> cardList = null;
                    try {
                        cardList = sdkData.getComvivaSdk().getAllCards();
                        if ((cardList != null) && (cardList.size() == 1) && (cardList.get(0).getCardState().equals(CardState.ACTIVE))) {
                            sdkData.getComvivaSdk().setDefaultCard(cardList.get(0));
                        }
                    } catch (Exception e) {
                        Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
                    }*/
                } else {
                    responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(verifyOTPResponse.getResponseCode()), verifyOTPResponse.getResponseMessage()));
                }
            } else if (result instanceof CardLCMOperationResponse) {
                ResponseListener responseListener = (ResponseListener) listener;
                CardLCMOperationResponse cardLcmOperationResponse = (CardLCMOperationResponse) result;
                handleCardLCMSuccessResponse(cardLcmOperationResponse, responseListener);
            } else if (result instanceof TransactionHistoryRegisterMdesResponse) {
                TransactionHistoryRegisterMdesResponse transactionHistoryRegisterMdesResponse = (TransactionHistoryRegisterMdesResponse) result;
                if (Constants.HTTP_RESPONSE_CODE_200.equals(transactionHistoryRegisterMdesResponse.getResponseCode()) && null != transactionHistoryRegisterMdesResponse.getRegistrationStatus()) {
                    CommonUtil.setSharedPreference(tokenRef, transactionHistoryRegisterMdesResponse.getRegistrationStatus(), Constants.SHARED_PREF_MDES_CARD_STATUS_DETAILS);
                }
            }
        } catch (Exception e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
            handleError(listener);
        }
    }




    private void handleCardLCMSuccessResponse(CardLCMOperationResponse cardLcmOperationResponse, ResponseListener responseListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(cardLcmOperationResponse.getResponseCode())) {
            switch (cardLcmRequestParam.getCardLcmOperation()) {
                case DELETE:
                    try {
                        sdkData.getMcbp().getCardManager().deleteCard(sdkData.getMcbp().getCardManager().getCardById(paymentCard.getCardUniqueId()));
                        responseListener.onSuccess();
                    } catch (RolloverInProgressException e1) {
                        Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e1));
                        responseListener.onError(SdkErrorImpl.getInstance(SdkErrorImpl.SW_SDK_ROLLOVER_IN_PROGRESS, e1.getMessage()));
                    } catch (Exception e2) {
                        Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e2));
                        responseListener.onError(SdkErrorImpl.getInstance(SdkErrorImpl.SW_SDK_INTERNAL_ERROR, e2.getMessage()));
                    }
                    break;
                case SUSPEND:
                    try {
                        sdkData.getMcbp().getCardManager().suspendCard(sdkData.getMcbp().getCardManager().getCardById(paymentCard.getCardUniqueId()));
                        responseListener.onSuccess();
                    } catch (RolloverInProgressException e1) {
                        Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e1));
                        responseListener.onError(SdkErrorImpl.getInstance(SdkErrorImpl.SW_SDK_ROLLOVER_IN_PROGRESS, e1.getMessage()));
                    } catch (Exception e2) {
                        Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e2));
                        responseListener.onError(SdkErrorImpl.getInstance(SdkErrorImpl.SW_SDK_INTERNAL_ERROR, e2.getMessage()));
                    }
                    break;
                case RESUME:
                    try {
                        sdkData.getMcbp().getCardManager().activateCard(sdkData.getMcbp().getCardManager().getCardById(paymentCard.getCardUniqueId()));
                        responseListener.onSuccess();
                    } catch (RolloverInProgressException e1) {
                        Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e1));
                        responseListener.onError(SdkErrorImpl.getInstance(SdkErrorImpl.SW_SDK_ROLLOVER_IN_PROGRESS, e1.getMessage()));
                    } catch (Exception e2) {
                        Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e2));
                        responseListener.onError(SdkErrorImpl.getInstance(SdkErrorImpl.SW_SDK_INTERNAL_ERROR, e2.getMessage()));
                    }
                    break;
            }
        } else {
            responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(cardLcmOperationResponse.getResponseCode()), cardLcmOperationResponse.getResponseMessage()));
        }
    }


    @Override
    public void onRequestError(String message, Object listener) {

        handleError(listener, message);
    }


    /**
     * Checks that Card is eligible for digitization or not.
     *
     * @param cardEligibilityRequestParam Eligibility request
     * @param checkEligibilityListener    Eligibility Response
     */
    void checkCardEligibilityMdes(CardEligibilityRequestParam cardEligibilityRequestParam, final CheckCardEligibilityListener checkEligibilityListener) throws SdkException {

        checkEligibilityListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.checkCardEligibilityMdes(cardEligibilityRequestParam, checkEligibilityListener);
    }


    /**
     * Fetches Asset's value from payment App Server.
     *
     * @param assetId Asset ID
     * @return GetAssetResponse object
     */
    public void getAsset(String assetId, GetAssetListener getAssetListener) throws SdkException {

        getAssetListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.getAssetsMdes(assetId, null, getAssetListener);
    }


    void generateOTP(CardType cardType, String tokenUniqueReference, String authenticationCodeId, final ResponseListener responseListener) throws SdkException {

        responseListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.generateOTP(cardType, tokenUniqueReference, authenticationCodeId, responseListener);
        this.tokenRef = tokenUniqueReference;  // to be removed
    }


    void verifyOTP(CardType cardType, String provisionID, String otpValue, ResponseListener responseListener) throws SdkException {

        responseListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.verifyOtp(cardType, provisionID, otpValue, responseListener);
    }


    /**
     * Digitize the card.
     *
     * @param digitizationRequestParam Digitization Request
     * @param digitizationListener     UI Listener
     */
    void digitize(DigitizationRequestParam digitizationRequestParam, final DigitizationListener digitizationListener) throws SdkException {

        digitizationListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.digitizeMdes(checkCardEligibilityResponse, digitizationListener);
    }


    void getStepUpOptions(String stepUpID, StepUpListener stepUpListener) throws SdkException {

        stepUpListener.onStarted();
        networkApi.setServerAuthenticateListener(this);
        networkApi.stepUpOptions(stepUpID, stepUpListener);
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

    /*   */


    /**
     * This API is used to Suspend, UnSuspend and Delete Token
     *//*
    void performCardLcm(final ArrayList<PaymentCard> cardList,
                        final CardLcmOperation cardLcmOperation,
                        final CardLcmReasonCode reasonCode,
                        final ResponseListener responseListener) {

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
            jsCardLcmReq.put("responseCode", reasonCode.name());
            jsCardLcmReq.put("reason", "Not Specified");
            jsCardLcmReq.put("operation", (cardLcmOperation == CardLcmOperation.RESUME) ? "UNSUSPEND" : cardLcmOperation.name());
        } catch (JSONException e) {
            responseListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            responseListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
            return;
        }
        class CardLcmTask extends AsyncTask<Void, Void, HttpResponse> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                responseListener.onStarted();
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
                            responseListener.onError(SdkErrorImpl.getInstance(jsResponse.getInt("reasonCode"), jsResponse.getString("message")));
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
                                    responseListener.onSuccess();
                                    for (int j = 0; j < cardList.size(); j++) {
                                        sdkData.getMcbp().getCardManager().deleteCard((Card) cardList.get(j));
                                    }
                                    //McbpCardApi.deleteCard(tokenUniqueRef, false);
                                    break;
                                case SUSPEND:
                                    if (token.getString("status").equals("SUSPENDED")) {
                                        // McbpCardApi.suspendCard(tokenUniqueRef);
                                        // McbpCardApi.remoteWipeSuksForCard(tokenUniqueRef);
                                        responseListener.onSuccess();
                                        for (int j = 0; j < cardList.size(); j++) {
                                            sdkData.getMcbp().getCardManager().suspendCard((Card) cardList.get(j));
                                        }
                                    }
                                    break;
                                case RESUME:
                                    if (token.getString("status").equals("ACTIVE")) {
                                        responseListener.onSuccess();
                                        for (int j = 0; j < cardList.size(); j++) {
                                            sdkData.getMcbp().getCardManager().activateCard((Card) cardList.get(j));
                                        }
                                    }
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        responseListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    } catch (Exception e) {
                        responseListener.onError(SdkErrorStandardImpl.SDK_TASK_ALREADY_IN_PROGRESS);
                    }
                } else {
                    responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }
            }
        }
        CardLcmTask cardLcmTask = new CardLcmTask();
        cardLcmTask.execute();
    }*/
    private void handleError(Object listener, String... message) {

        if (message.length > 0) {
            if (listener instanceof CheckCardEligibilityListener) {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            } else if (listener instanceof GetAssetListener) {
                GetAssetListener getAssetListener = (GetAssetListener) listener;
                getAssetListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            } else if (listener instanceof DigitizationListener) {
                DigitizationListener digitizationListener = (DigitizationListener) listener;
                digitizationListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            } else if (listener instanceof ResponseListener) {
                ResponseListener responseListener = (ResponseListener) listener;
                responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
            }
        } else {
            if (listener instanceof CheckCardEligibilityListener) {
                if (listener instanceof CheckCardEligibilityListener) {
                    CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                    checkCardEligibilityListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                } else if (listener instanceof GetAssetListener) {
                    GetAssetListener getAssetListener = (GetAssetListener) listener;
                    getAssetListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                } else if (listener instanceof DigitizationListener) {
                    DigitizationListener digitizationListener = (DigitizationListener) listener;
                    digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                } else if (listener instanceof ResponseListener) {
                    ResponseListener responseListener = (ResponseListener) listener;
                    responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
    }


    private ContentGuid parseGetAssetResponse(GetAssetResponse getAssetResponse) {

        ContentGuid contentGuid = new ContentGuid();
        if (null != getAssetResponse.getContentType()) {
            contentGuid.setContentType(AssetType.getType(getAssetResponse.getContentType()));
        }
        MediaContent[] mediaContentMdes = getAssetResponse.getMediaContents();
        MediaContent[] mediaContents = new MediaContent[mediaContentMdes.length];
        for (int i = 0; i < mediaContentMdes.length; i++) {
            mediaContents[i] = new MediaContent();
            mediaContents[i].setData(mediaContentMdes[i].getData());
            String contentType = mediaContentMdes[i].getAssetType().name();
            if (contentType.equalsIgnoreCase(Tags.IMG_PDF.getTag())) {
                mediaContents[i].setAssetType(AssetType.APPLICATION_PDF);
            } else {
                mediaContents[i].setAssetType(AssetType.getType(contentType));
            }
            switch (mediaContents[i].getAssetType()) {
                case IMAGE_PNG:
                case APPLICATION_PDF:
                    mediaContents[i].setHeight(mediaContentMdes[i].getHeight());
                    mediaContents[i].setWidth(mediaContentMdes[i].getWidth());
            }
        }
        contentGuid.setContent(mediaContents);
        return contentGuid;
    }


    private void handleCheckCardEligibilityResponse(CheckCardEligibilityResponse checkCardEligibilityResponse, CheckCardEligibilityListener checkCardEligibilityListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(checkCardEligibilityResponse.getResponseCode())) {
            sdkData.setCheckEligibilityPerformed(true);
            this.checkCardEligibilityResponse = checkCardEligibilityResponse;
            try {
                networkApi.setServerAuthenticateListener(this);
                networkApi.getAssetsMdes(checkCardEligibilityResponse.getTermsAndConditionsAssetId(), checkCardEligibilityListener);
            } catch (SdkException e) {
                Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                checkCardEligibilityListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
            }
        } else {
            checkCardEligibilityListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(checkCardEligibilityResponse.getResponseCode()), checkCardEligibilityResponse.getResponseMessage()));
        }
    }


    private void handleGetAssetResponse(GetAssetResponse getAssetResponse, Object listener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(getAssetResponse.getResponseCode())) {
            ContentGuid contentGuid = parseGetAssetResponse(getAssetResponse);
            if (listener instanceof GetAssetListener) {
                GetAssetListener getAssetListener = (GetAssetListener) listener;
                getAssetListener.onCompleted(contentGuid);
            } else {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onTermsAndConditionsRequired(contentGuid);
            }
        } else {
            if (listener instanceof GetAssetListener) {
                GetAssetListener getAssetListener = (GetAssetListener) listener;
                getAssetListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(getAssetResponse.getResponseCode()), getAssetResponse.getResponseMessage()));
            } else {
                CheckCardEligibilityListener checkCardEligibilityListener = (CheckCardEligibilityListener) listener;
                checkCardEligibilityListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(getAssetResponse.getResponseCode()), getAssetResponse.getResponseMessage()));
            }
        }
    }


    private List<StepUpRequest> prepareStepUpResponseObject(List<AuthenticationMethod> authenticationMethodList) {
        // Map Step up request List from visa to comviva SdDK  stepUpList
        List<StepUpRequest> stepUpRequestsList = new ArrayList<>();
        for (AuthenticationMethod authenticationMethod : authenticationMethodList) {
            StepUpRequest stepUpRequest = new StepUpRequest(authenticationMethod.getId(), authenticationMethod.getType(), authenticationMethod.getValue());
            stepUpRequestsList.add(stepUpRequest);
        }
        return stepUpRequestsList;
    }


    private void handleDigitizeMdesResponse(DigitizeMdesResponse digitizeMdesResponse, DigitizationListener digitizationListener) {

        if (Constants.HTTP_RESPONSE_CODE_200.equals(digitizeMdesResponse.getResponseCode())) {
            if (Tags.REQUIRE_ADDITIONAL_AUTHENTICATION.getTag().equals(digitizeMdesResponse.getDecision())) {
                digitizationListener.onRequireAdditionalAuthentication(digitizeMdesResponse.getTokenUniqueReference(), digitizeMdesResponse.getTokenUniqueReference(), prepareStepUpResponseObject(digitizeMdesResponse.getAuthenticationMethodList()), digitizeMdesResponse.getProductConfig());
            } else if (Tags.APPROVED.getTag().equals(digitizeMdesResponse.getDecision())) {
                digitizationListener.onApproved(digitizeMdesResponse.getPanUniqueReference(), digitizeMdesResponse.getPanUniqueReference());
                ArrayList<PaymentCard> cardList = null;
                try {
                    cardList = sdkData.getComvivaSdk().getAllCards();
                    if ((cardList != null) && (cardList.size() == 1) && (cardList.get(0).getCardState().equals(CardState.ACTIVE))) {
                        sdkData.getComvivaSdk().setDefaultCard(cardList.get(0));
                    }
                } catch (Exception e) {
                    Log.d(Tags.DEBUG_LOG.getTag(), e.getMessage());
                }
            }
        } else {
            digitizationListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(digitizeMdesResponse.getResponseCode()), digitizeMdesResponse.getResponseMessage()));
        }
    }
}
