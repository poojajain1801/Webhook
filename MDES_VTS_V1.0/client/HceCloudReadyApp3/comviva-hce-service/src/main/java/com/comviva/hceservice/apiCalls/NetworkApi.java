package com.comviva.hceservice.apiCalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.listeners.CheckCardEligibilityListener;
import com.comviva.hceservice.listeners.DigitizationListener;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.GetCardMetaDataListener;
import com.comviva.hceservice.listeners.TokenDataUpdateListener;
import com.comviva.hceservice.listeners.StepUpListener;
import com.comviva.hceservice.pojo.ConfirmProvisioningResponse;
import com.comviva.hceservice.pojo.GetAssetResponse;
import com.comviva.hceservice.pojo.digitizeMdes.DigitizeMdesResponse;
import com.comviva.hceservice.pojo.GenerateOTPResponse;
import com.comviva.hceservice.pojo.CardLCMOperationResponse;
import com.comviva.hceservice.pojo.RegisterUserResponse;
import com.comviva.hceservice.pojo.StepUpResponse;
import com.comviva.hceservice.pojo.tokenstatusupdate.TokenUpdateResponse;
import com.comviva.hceservice.pojo.UnRegisterDeviceResponse;
import com.comviva.hceservice.pojo.VerifyOTPResponse;
import com.comviva.hceservice.pojo.checkcardeligibility.CheckCardEligibilityResponse;
import com.comviva.hceservice.pojo.enrollpanVts.EnrollPanResponse;
import com.comviva.hceservice.pojo.gettermsandconditionvts.GetTermsAndCondtionVtsResponse;
import com.comviva.hceservice.pojo.registerdevice.RegisterDeviceResponse;
import com.comviva.hceservice.pojo.transactionhistorymdes.TransactionHistoryRegisterMdesResponse;
import com.comviva.hceservice.requestobjects.CardEligibilityRequestParam;
import com.comviva.hceservice.requestobjects.CardLcmRequestParam;
import com.comviva.hceservice.requestobjects.DigitizationRequestParam;
import com.comviva.hceservice.requestobjects.RegisterRequestParam;
import com.comviva.hceservice.responseobject.transactionhistory.TransactionHistoryData;
import com.comviva.hceservice.security.RSAUtil;
import com.comviva.hceservice.listeners.TransactionHistoryListener;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mastercard.mcbp_android.R;
import com.visa.cbp.external.common.CardMetaData;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.external.enp.ProvisionResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class NetworkApi {

    private JsonObjectRequest request = null;
    private ServerResponseListener serverResponseListener;
    private static final int REQUEST_TIMEOUT = 600000;
    private RequestQueue requestQueue;
    private SDKData sdkData;


    public void setServerAuthenticateListener(ServerResponseListener listener) {

        serverResponseListener = listener;
    }


    public NetworkApi() {

        sdkData = SDKData.getInstance();
        SharedPreferences sharedPrefConf = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_CONF, Context.MODE_PRIVATE);
        if (sharedPrefConf.getBoolean(CommonUtil.encrypt(Constants.KEY_HTTPS_ENABLED), false)) {
            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(sdkData.getContext(), hurlStack);
        } else {
            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(sdkData.getContext());
        }
    }


    private HurlStack hurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            try {
                httpsURLConnection.setSSLSocketFactory(newSslSocketFactory());
                httpsURLConnection.setHostnameVerifier(getHostnameVerifier(url));
            } catch (Exception e) {
                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(e));
            }
            return httpsURLConnection;
        }
    };


    public void registerUser(String userID, String clientDeviceID, final ResponseListener registerUserListener) throws SdkException {

        String url = UrlUtil.getRegisterUserUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getRegisterUserParams(userID, clientDeviceID);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    RegisterUserResponse registerUserResponse = gson.fromJson(jsonObject.toString(), RegisterUserResponse.class);
                    serverResponseListener.onRequestCompleted(registerUserResponse, registerUserListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, registerUserListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void registerDevice(String clientDeviceId, RegisterRequestParam registerRequestParam, final ResponseListener responseListener) throws SdkException {

        String url = UrlUtil.getRegisterDeviceUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getRegisterDeviceParams(clientDeviceId, registerRequestParam);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    RegisterDeviceResponse registerDeviceResponse = gson.fromJson(jsonObject.toString(), RegisterDeviceResponse.class);
                    serverResponseListener.onRequestCompleted(registerDeviceResponse, responseListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, responseListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void checkCardEligibilityMdes(CardEligibilityRequestParam cardEligibilityRequestParam, final CheckCardEligibilityListener checkEligibilityListener) throws SdkException {

        String url = UrlUtil.getCheckCardEligibilityUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getCheckCardEligibilityParams(cardEligibilityRequestParam);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    CheckCardEligibilityResponse checkCardEligibilityResponse = gson.fromJson(jsonObject.toString(), CheckCardEligibilityResponse.class);
                    serverResponseListener.onRequestCompleted(checkCardEligibilityResponse, checkEligibilityListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, checkEligibilityListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void getAssetsMdes(String assetID, final CheckCardEligibilityListener checkCardEligibilityListener, final GetAssetListener... getAssetListener) throws SdkException {

        String url = UrlUtil.getAssetUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getAssetsMdes(assetID);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    GetAssetResponse getAssetResponse = gson.fromJson(jsonObject.toString(), GetAssetResponse.class);
                    if (getAssetListener.length > 0) {
                        serverResponseListener.onRequestCompleted(getAssetResponse, getAssetListener[0]);
                    } else {
                        serverResponseListener.onRequestCompleted(getAssetResponse, checkCardEligibilityListener);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (getAssetListener.length > 0) {
                    handleErrorFromServer(volleyError, getAssetListener[0]);
                } else {
                    handleErrorFromServer(volleyError, checkCardEligibilityListener);
                }
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void enrollPanVts(CardEligibilityRequestParam cardEligibilityRequestParam, final CheckCardEligibilityListener checkEligibilityListener) throws SdkException {

        String url = UrlUtil.getVTSEnrollPanUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject encryptedString;
        JSONObject reqString = RequestParamsManager.getEnrollPanVtsParams(cardEligibilityRequestParam);
        try {
            encryptedString = new JSONObject(RSAUtil.doMeth(sdkData.getContext(), reqString.toString()));
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        }
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, encryptedString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    EnrollPanResponse enrollPanResponse = gson.fromJson(jsonObject.toString(), EnrollPanResponse.class);
                    serverResponseListener.onRequestCompleted(enrollPanResponse, checkEligibilityListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, checkEligibilityListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void digitizeMdes(CheckCardEligibilityResponse checkCardEligibilityResponse, final DigitizationListener digitizationListener) throws SdkException {
        String url = UrlUtil.getContinueDigitizationUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getDigitizationMdes(checkCardEligibilityResponse);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    DigitizeMdesResponse digitizeMdesResponse = gson.fromJson(jsonObject.toString(), DigitizeMdesResponse.class);
                    serverResponseListener.onRequestCompleted(digitizeMdesResponse, digitizationListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, digitizationListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void tokenUpdateVts(PaymentCard paymentCard, final TokenDataUpdateListener tokenDataUpdateListener) throws SdkException {

        String url = UrlUtil.getVTSTokenStatus();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getTokenStatusVts(paymentCard);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    TokenUpdateResponse tokenUpdateResponse = gson.fromJson(jsonObject.toString(), TokenUpdateResponse.class);
                    serverResponseListener.onRequestCompleted(tokenUpdateResponse, tokenDataUpdateListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, tokenDataUpdateListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void getCardMetaDataVts(String vPanEnrollmentID, final GetCardMetaDataListener getCardMetaDataListener) throws SdkException {

        String url = UrlUtil.getVTSCardMetaDataUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getCardMetaDataParams(vPanEnrollmentID);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    CardMetaData cardMetaData = gson.fromJson(jsonObject.toString(), CardMetaData.class);
                    serverResponseListener.onRequestCompleted(cardMetaData, getCardMetaDataListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, getCardMetaDataListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void getTermsAndConditionVts(String guid, final CheckCardEligibilityListener checkEligibilityListener, final GetAssetListener... getAssetListener) throws SdkException {

        String url = UrlUtil.getVTSContentUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getTermsAndConditionVtsParams(guid);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    GetTermsAndCondtionVtsResponse getTermsAndCondtionVtsResponse = gson.fromJson(jsonObject.toString(), GetTermsAndCondtionVtsResponse.class);
                    if (getAssetListener.length > 0) {
                        serverResponseListener.onRequestCompleted(getTermsAndCondtionVtsResponse, getAssetListener[0]);
                    } else {
                        serverResponseListener.onRequestCompleted(getTermsAndCondtionVtsResponse, checkEligibilityListener);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (getAssetListener.length > 0) {
                    handleErrorFromServer(volleyError, getAssetListener[0]);
                } else {
                    handleErrorFromServer(volleyError, checkEligibilityListener);
                }
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void provisionVts(DigitizationRequestParam digitizationRequestParam, EnrollPanResponse enrollPanResponse, final DigitizationListener digitizationListener) throws SdkException {

        String url = UrlUtil.getVTSProvisionTokenUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getProvisionVtsParams(digitizationRequestParam, enrollPanResponse);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    try {
                        if (!Constants.HTTP_RESPONSE_CODE_200.equals(jsonObject.get(Tags.RESPONSE_CODE.getTag()))) {
                            handleErrorFromServer(null, digitizationListener, jsonObject.getString(Tags.MESSAGE.getTag()));
                        } else {
                            ProvisionResponse provisionResponse = gson.fromJson(jsonObject.toString(), ProvisionResponse.class);
                            serverResponseListener.onRequestCompleted(provisionResponse, digitizationListener);
                        }
                    } catch (JSONException e) {
                        Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
                        digitizationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, digitizationListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void confirmProvisionVts(ProvisionAckRequest provisionAckRequest, String vProvisionTokenID, final DigitizationListener digitizationListener) throws SdkException {

        String url = UrlUtil.getVTSConfirmProvisioningUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getCofirmProvisionVtsParams(provisionAckRequest, vProvisionTokenID);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    ConfirmProvisioningResponse confirmProvisioningResponse = gson.fromJson(jsonObject.toString(), ConfirmProvisioningResponse.class);
                    serverResponseListener.onRequestCompleted(confirmProvisioningResponse, digitizationListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, digitizationListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void performCardLCMMdesVts(CardLcmRequestParam cardLcmRequestParam, final ResponseListener responseListener) throws SdkException {

        String url;
        if (CardType.VTS == cardLcmRequestParam.getPaymentCard().getCardType()) {
            url = UrlUtil.getCardLifeCycleManagementVtsUrl();
        } else {
            url = UrlUtil.getCardLifeCycleManagementMdesUrl();
        }
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getPerformCardLCMMdesVtsParams(cardLcmRequestParam);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    CardLCMOperationResponse cardLCMOperationResponse = gson.fromJson(jsonObject.toString(), CardLCMOperationResponse.class);
                    serverResponseListener.onRequestCompleted(cardLCMOperationResponse, responseListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, responseListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void UnregisterDevice(String imei, String userID, final ResponseListener responseListener) throws SdkException {

        String url = UrlUtil.getUnRegisterDeviceUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getUnRegisterDeviceParams(imei, userID);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    UnRegisterDeviceResponse unRegisterDeviceResponse = gson.fromJson(jsonObject.toString(), UnRegisterDeviceResponse.class);
                    serverResponseListener.onRequestCompleted(unRegisterDeviceResponse, responseListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, responseListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void generateOTP(CardType cardType, String provisionID, String stepUpRequestId, final ResponseListener responseListener) throws SdkException {

        String url = UrlUtil.getRequestOtpUrl(cardType);
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getGenerateOtpParams(cardType, provisionID, stepUpRequestId);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    GenerateOTPResponse generateOTPResponse = gson.fromJson(jsonObject.toString(), GenerateOTPResponse.class);
                    serverResponseListener.onRequestCompleted(generateOTPResponse, responseListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, responseListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void verifyOtp(CardType cardType, String provisionID, String otpValue, final ResponseListener responseListener) throws SdkException {

        String url = UrlUtil.getVerifyOtpUrl(cardType);
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getVerifyOtpParams(cardType, provisionID, otpValue);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    VerifyOTPResponse verifyOTPResponse = gson.fromJson(jsonObject.toString(), VerifyOTPResponse.class);
                    serverResponseListener.onRequestCompleted(verifyOTPResponse, responseListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, responseListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void stepUpOptions(String stepUpID, final StepUpListener stepUpListener) throws SdkException {

        String url = UrlUtil.getStepUpUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getStepUpParams(stepUpID);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    StepUpResponse stepUpResponse = gson.fromJson(jsonObject.toString(), StepUpResponse.class);
                    serverResponseListener.onRequestCompleted(stepUpResponse, stepUpListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, stepUpListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void getTransactionHistory(final PaymentCard paymentCard, final int count, final TransactionHistoryListener transactionHistoryListener) throws SdkException {

        String url;
        if (CardType.MDES.equals(paymentCard.getCardType())) {
            url = UrlUtil.getMDESTransactionHistory();
        } else {
            url = UrlUtil.getVTSTransactionHistory();
        }
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getTransactionHistoryParams(paymentCard, count);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    TransactionHistoryData transactionHistoryData = gson.fromJson(jsonObject.toString(), TransactionHistoryData.class);
                    serverResponseListener.onRequestCompleted(transactionHistoryData, transactionHistoryListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, transactionHistoryListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void getRegisterTransactionHistoryMdes(String tokenReference) throws SdkException {

        String url = UrlUtil.getRegisterForTransactionHistoryMdes();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getRegisterTransactionHistoryForMdesParams(tokenReference);
        Log.d(Tags.REQUEST_LOG.getTag(), reqString.toString());
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.d(Tags.RESPONSE_LOG.getTag(), jsonObject.toString());
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    TransactionHistoryRegisterMdesResponse transactionHistoryRegisterMdesResponse = gson.fromJson(jsonObject.toString(), TransactionHistoryRegisterMdesResponse.class);
                    serverResponseListener.onRequestCompleted(transactionHistoryRegisterMdesResponse, null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                handleErrorFromServer(volleyError, null);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    /*Handled Error from server*/
    private void handleErrorFromServer(VolleyError error, Object listener, String... message) {

        if (error != null) {
            if (error instanceof TimeoutError) {
                serverResponseListener.onRequestError(sdkData.getContext().getString(R.string.error_timeout), listener);
            } else if (error instanceof NoConnectionError) {
                serverResponseListener.onRequestError(sdkData.getContext().getString(R.string.error_connection), listener);
            } else if (error instanceof ServerError || error instanceof NetworkError) {
                serverResponseListener.onRequestError(sdkData.getContext().getString(R.string.error_network), listener);
            } else if (error instanceof ParseError) {
                serverResponseListener.onRequestError(sdkData.getContext().getString(R.string.error_parse), listener);
            } else {
                serverResponseListener.onRequestError(sdkData.getContext().getString(R.string.error_general), listener);
            }
        } else {
            serverResponseListener.onRequestError(message[0].toString(), listener);
        }
    }


    private HostnameVerifier getHostnameVerifier(final URL url) {

        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                // return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                Log.d(Tags.DEBUG_LOG.getTag(), url.getHost());
                Log.d(Tags.DEBUG_LOG.getTag(), url.toString());
                Log.d(Tags.DEBUG_LOG.getTag(), String.valueOf(hv.verify(url.getHost(), session)));
                return hv.verify(url.getHost(), session);
            }
        };
    }


    private SSLSocketFactory newSslSocketFactory() {

        try {
            Certificate ca = null;
            try {
                ca = CommonUtil.getCertificateFromKeystore(Constants.PAYMENT_APP_CERTIFICATE);
            } catch (Exception e) {
            }
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;
            keyStore = KeyStore.getInstance(keyStoreType);
            if (null != keyStore) {
                keyStore.load(null, null);
            }
            if (null != keyStore) {
                keyStore.setCertificateEntry("ca", ca);
            } else {
                return null;
            }
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = null;
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            if (null != tmf) {
                tmf.init(keyStore);
            }
            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            if (null != sslContext && null != tmf) {
                sslContext.init(null, tmf.getTrustManagers(), null);
            } else {
                return null;
            }
            if (null != sslContext.getSocketFactory()) {
                return sslContext.getSocketFactory();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            return null;
        }
    }
}
