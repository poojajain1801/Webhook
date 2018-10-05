package com.comviva.hceservice.digitizationApi;

import android.os.AsyncTask;
import android.util.Log;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.listeners.CheckCardEligibilityListener;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.listeners.DigitizationListener;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.GetCardMetaDataListener;
import com.comviva.hceservice.listeners.TokenDataUpdateListener;
import com.comviva.hceservice.listeners.StepUpListener;
import com.comviva.hceservice.requestobjects.CardEligibilityRequestParam;
import com.comviva.hceservice.requestobjects.CardLcmRequestParam;
import com.comviva.hceservice.requestobjects.DigitizationRequestParam;
import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.LuhnUtil;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.google.gson.Gson;
import com.visa.cbp.external.common.CardMetadataUpdateResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains all Digitization APIs.
 */
public class Digitization {

    private DigitizationMdes digitizationMdes;
    private DigitizationVts digitizationVts;
    private static SDKData sdkData;


    private Digitization() {
    }


    /**
     * Returns singleton instance of This class.
     *
     * @return Instance of this class
     */
    public static Digitization getInstance() {
        sdkData = SDKData.getInstance();
        if (null == sdkData.getDigitization()) {
            Digitization digitization = new Digitization();
            sdkData.setDigitization(digitization);
        }
        return sdkData.getDigitization();
    }


    /**
     * Checks that Card is eligible for digitization or not.
     *
     * @param cardEligibilityRequestParam Eligibility request
     * @param checkEligibilityListener    Eligibility Response
     */
    public void checkCardEligibility(CardEligibilityRequestParam cardEligibilityRequestParam, final CheckCardEligibilityListener checkEligibilityListener) {

        try {
            resetDigitization();
            String cardNumber = cardEligibilityRequestParam.getAccountNumber();
            // Validate Card number
            if (cardNumber.length() < 13 || cardNumber.length() > 19 || !LuhnUtil.checkLuhn(cardNumber)) {
                checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_INVALID_CARD_NUMBER);
                return;
            }
            CardType cardType = CardType.checkCardType(cardNumber);
            switch (cardType) {
                case MDES:
                    digitizationMdes = new DigitizationMdes();
                    digitizationMdes.checkCardEligibilityMdes(cardEligibilityRequestParam, checkEligibilityListener);
                    break;
                case VTS:
                    digitizationVts = new DigitizationVts();
                    digitizationVts.enrollPanVts(cardEligibilityRequestParam, checkEligibilityListener);
                    break;
                default:
                    checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_UNSUPPORTED_SCHEME);
            }
        } catch (Exception e) {
            checkEligibilityListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    /**
     * Digitize the card.
     *
     * @param digitizationRequestParam Digitization Request
     * @param digitizationListener     UI Listener
     */
    public void digitize(DigitizationRequestParam digitizationRequestParam, final DigitizationListener digitizationListener) {

        try {
            // Check Card Eligibility is not invoked earlier
            if (digitizationMdes == null && digitizationVts == null) {
                digitizationListener.onError(SdkErrorStandardImpl.SDK_CARD_ELIGIBILITY_NOT_PERFORMED);
                return;
            }
            switch (digitizationRequestParam.getCardType()) {
                case MDES:
                    if (null == digitizationMdes) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                        break;
                    } else if (!sdkData.isCheckEligibilityPerformed()) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_CARD_ELIGIBILITY_NOT_PERFORMED);
                        break;
                    } else {
                        digitizationMdes.digitize(digitizationRequestParam, digitizationListener);
                        break;
                    }
                case VTS:
                    if (null == digitizationVts) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                        return;
                    } else if (!sdkData.isEnrollPanPerformed()) {
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_CARD_ELIGIBILITY_NOT_PERFORMED);
                        break;
                    } else {
                        digitizationVts.provisionToken(digitizationRequestParam, digitizationListener);
                        break;
                    }
                case UNKNOWN:
                    digitizationListener.onError(SdkErrorStandardImpl.SDK_UNSUPPORTED_SCHEME);
            }
        } catch (Exception e) {
            digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        } finally {
            resetDigitization();
        }
    }


    private void resetDigitization(){
        digitizationMdes = null;
        digitizationVts = null;
        sdkData.setEnrollPanPerformed(false);
        sdkData.setCheckEligibilityPerformed(false);
    }





    /**
     * This API is used to Suspend, UnSuspend and Delete Token. <br>
     * At a time, you can provide one type of card only. <br>
     * Note - In case of MasterCard, you can provide more than one card. <br>
     * In case of Visa, you can provide only one card<br>
     *
     * @param cardLcmRequestParam Card Life Cycle Management Request
     * @param responseListener    UI Listener
     */
    public void performCardLcm(final CardLcmRequestParam cardLcmRequestParam, final ResponseListener responseListener) throws SdkException {
        // If list contains only MasterCard
        if (null != cardLcmRequestParam.getPaymentCard()) {
            if (CardType.MDES.equals(cardLcmRequestParam.getPaymentCard().getCardType())) {
                if (digitizationMdes == null) {
                    digitizationMdes = new DigitizationMdes();
                }
                digitizationMdes.performCardLcm(cardLcmRequestParam, responseListener);
            } else if (CardType.VTS.equals(cardLcmRequestParam.getPaymentCard().getCardType())) {
                // List contains only one visa Card
                if (digitizationVts == null) {
                    digitizationVts = new DigitizationVts();
                }
                digitizationVts.performCardLcm(cardLcmRequestParam, responseListener);
            }
        }
    }





    /**
     * Checks token's current status and update accordingly.
     * <p>
     * Note- This API is only applicable for VISA .
     *
     * @param paymentCard             Payment Card need to be checked
     * @param tokenDataUpdateListener Listener
     */
    public void getTokenStatus(final PaymentCard paymentCard, final TokenDataUpdateListener tokenDataUpdateListener) throws SdkException {

        if (digitizationVts == null) {
            digitizationVts = new DigitizationVts();
        }
        digitizationVts.getTokenStatus(paymentCard, tokenDataUpdateListener);
    }


    /**
     * This API allows clients to retrieve metadata related to the token.
     * <p>
     * Note- This API is only applicable for VISA .
     *
     * @param vPanEnrollmentID        vPanEnrollmentId of Card
     * @param getCardMetaDataListener Listener
     */
    public void getCardMetaData(final String vPanEnrollmentID, final GetCardMetaDataListener getCardMetaDataListener) {
        /* final TokenData tokenData = (TokenData) paymentCard.getCurrentCard();*/
        final JSONObject jsonCardMetaDataRequest = new JSONObject();
        try {
            /*ComvivaSdk comvivaSdk =  ComvivaSdk.getInstance(null);
            SharedPreferences pref = comvivaSdk.getApplicationContext().getSharedPreferences(Tags.VPAN_ENROLLMENT_ID.getTag(), Context.MODE_PRIVATE);
            String vPanEnrollId = pref.getString(tokenData.getVProvisionedTokenID(), null); // getting String
            if(vPanEnrollId != null)
            {
                jsonCardMetaDataRequest.put(Tags.vpan_ENROLLMENT_ID.getTag(),vPanEnrollId);
            }*/
            jsonCardMetaDataRequest.put(Tags.vpan_ENROLLMENT_ID.getTag(), vPanEnrollmentID);
        } catch (Exception e) {
            getCardMetaDataListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }
        class GetCardMetaDataTask extends AsyncTask<Void, Void, HttpResponse> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                if (getCardMetaDataListener != null) {
                    getCardMetaDataListener.onStarted();
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
                        try {
                            JSONObject jsGetCardMetaDataResponse = new JSONObject(httpResponse.getResponse());
                            CardMetaData cardMetaData = parseGetMetaDataResponse(jsGetCardMetaDataResponse);
                            if (getCardMetaDataListener != null) {
                                getCardMetaDataListener.onSuccess(cardMetaData);
                            }
                        } catch (JSONException e) {
                            getCardMetaDataListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                        } catch (Exception e) {
                            getCardMetaDataListener.onError(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR);
                        }
                    } else {
                        getCardMetaDataListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                    }
                    // Ideally response should be parsed like below
                 /*   try {
                        JSONObject jsGetCardMetaDataResponse = new JSONObject(httpResponse.getResponse());
                        if (jsGetCardMetaDataResponse.has(Tags.RESPONSE_CODE.getTag()) && !jsGetCardMetaDataResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            responseListener.onError(SdkErrorImpl.getInstance(jsGetCardMetaDataResponse.getInt(Tags.RESPONSE_CODE.getTag()),
                                    jsGetCardMetaDataResponse.getString("message")));
                            return;
                        }
                        if (jsGetCardMetaDataResponse.has(Tags.RESPONSE_CODE.getTag()) && jsGetCardMetaDataResponse.getString(Tags.RESPONSE_CODE.getTag()).equalsIgnoreCase("200")) {
                            parseGetMetaDataResponse(jsGetCardMetaDataResponse, tokenData);
                            if (responseListener != null) {
                                responseListener.onSuccess();
                            }
                        }
                    } catch (JSONException e) {
                        responseListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } else {
                    responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }*/
                } catch (Exception e) {
                    getCardMetaDataListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
        GetCardMetaDataTask getCardMetaDataTask = new GetCardMetaDataTask();
        getCardMetaDataTask.execute();
    }


    private CardMetaData parseGetMetaDataResponse(JSONObject jsGetMetaDataResponse) throws JSONException, Exception {

        Gson gson = new Gson();
        final CardMetadataUpdateResponse cardMetadataUpdateResponse = gson.fromJson(jsGetMetaDataResponse.toString(), CardMetadataUpdateResponse.class);
        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        boolean updateStatus = visaPaymentSDK.updateCardMetaData(cardMetadataUpdateResponse);
        CardMetaData cardMetaData = null;
        if (jsGetMetaDataResponse.has("cardMetaData")) {
            cardMetaData = gson.fromJson(jsGetMetaDataResponse.get("cardMetaData").toString(), CardMetaData.class);
        }
        return cardMetaData;
    }





    public void changePin(final String tokenUniqueReference,
                          final String oldPin,
                          final String newPin) {

        try {
            if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
                // MdesMcbpWalletApi.changeWalletPin(oldPin.getBytes(), newPin.getBytes());
            } else {
                //  McbpCardApi.changePin(tokenUniqueReference, oldPin.getBytes(), newPin.getBytes());
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        } finally {
            // RemoteManagementHandler.getInstance().clearPendingAction();
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
                // MdesMcbpWalletApi.setWalletPin(newPin.getBytes());
            } else {
                // McbpCardApi.setPin(tokenUniqueReference, newPin.getBytes());
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        } finally {
            // RemoteManagementHandler.getInstance().clearPendingAction();
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
                    comvivaSdk.replenishCard(paymentCard, listener);
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
     * @param id               id of the resource
     * @param getAssetListener Listener
     */
    public void getContent(CardType cardType, final String id, GetAssetListener getAssetListener) throws SdkException {

        if (cardType.equals(CardType.VTS)) {
            if (digitizationVts == null) {
                digitizationVts = new DigitizationVts();
            }
            digitizationVts.getContent(id, getAssetListener);
        } else if (cardType.equals(CardType.MDES)) {
            if (digitizationMdes == null) {
                digitizationMdes = new DigitizationMdes();
            }
            digitizationMdes.getAsset(id, getAssetListener);
        } else {
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    /**
     * generates Otp for step -up
     *
     * @param id               provision id of card
     * @param stepUpRequestId  stepUp Id  for the request
     * @param responseListener reponse listener to the request
     */
    public void generateOTP(CardType cardType, String id, String stepUpRequestId, final ResponseListener responseListener) throws SdkException {

        if (cardType.equals(CardType.VTS)) {
            if (digitizationVts == null) {
                digitizationVts = new DigitizationVts();
            }
            digitizationVts.generateOTP(cardType, id, stepUpRequestId, responseListener);
        } else if (cardType.equals(CardType.MDES)) {
            if (digitizationMdes == null) {
                digitizationMdes = new DigitizationMdes();
            }
            digitizationMdes.generateOTP(cardType, id, stepUpRequestId, responseListener);
        } else {
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    /**
     * generates Otp for step -up
     *
     * @param id               provision id of card
     * @param otpValue         Otp value
     * @param responseListener reponse listener to the request
     */
    public void verifyOTP(CardType cardType, String id, String otpValue, final ResponseListener responseListener) throws SdkException {

        if (cardType.equals(CardType.VTS)) {
            if (digitizationVts == null) {
                digitizationVts = new DigitizationVts();
            }
            digitizationVts.verifyOTP(cardType, id, otpValue, responseListener);
        } else if (cardType.equals(CardType.MDES)) {
            if (digitizationMdes == null) {
                digitizationMdes = new DigitizationMdes();
            }
            digitizationMdes.verifyOTP(cardType, id, otpValue, responseListener);
        } else {
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    /**
     * API to get Step up options
     *
     * @param stepUpID       step up ID for step up request
     * @param stepUpListener reponse listener to the request
     * @param cardType       type of card.mdes/vts
     */
    public void getStepUpOptions(CardType cardType, String stepUpID, StepUpListener stepUpListener) throws SdkException {

        if (cardType.equals(CardType.VTS)) {
            if (digitizationVts == null) {
                digitizationVts = new DigitizationVts();
            }
            digitizationVts.getStepUpOptions(stepUpID, stepUpListener);
        } else if (cardType.equals(CardType.MDES)) {
            if (digitizationMdes == null) {
                digitizationMdes = new DigitizationMdes();
            }
            digitizationMdes.getStepUpOptions(stepUpID, stepUpListener);
        } else {
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }
}
