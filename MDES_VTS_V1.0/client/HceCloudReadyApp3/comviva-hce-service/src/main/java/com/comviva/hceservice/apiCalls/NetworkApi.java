package com.comviva.hceservice.apiCalls;

import android.content.Context;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.digitizationApi.StepUpListener;
import com.comviva.hceservice.pojo.StepUpResponse;
import com.comviva.hceservice.pojo.VerifyOTPResponse;
import com.comviva.hceservice.util.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mastercard.mcbp_android.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkApi {
    private JsonObjectRequest request = null;
    private ServerResponseListener serverResponseListener;
    private static final int REQUEST_TIMEOUT = 500000;
    private Context context;
    private RequestQueue requestQueue;

    public void setServerAuthenticateListener(ServerResponseListener listener) {
        serverResponseListener = listener;
    }

    public NetworkApi(Context context) {
        this.context = context;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    public void verifyOtp(String provisionID, String otpValue, final ResponseListener responseListener) throws JSONException {
        String url = UrlUtil.getVerifyOtpUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getVerifyOtpParams(provisionID, otpValue);
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (serverResponseListener != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    VerifyOTPResponse verifyOTPResponse = gson.fromJson(jsonObject.toString(), VerifyOTPResponse.class);
                    serverResponseListener.onRequestCompleted(verifyOTPResponse,responseListener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handleErrorFromServer(volleyError,responseListener);
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    public void stepUpOptions(String provisionID, final StepUpListener stepUpListener) throws JSONException {
        String url = UrlUtil.getStepUpUrl();
        Log.d(Tags.URL_LOG.getTag(), url);
        JSONObject reqString = RequestParamsManager.getStepUpParams(provisionID);
        request = new JsonObjectRequest(Request.Method.POST, url, reqString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
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


    /*Handled Error from server*/
    private void handleErrorFromServer(VolleyError error, Object listener) {
        Log.d(Tags.RESPONSE_LOG.getTag(), error.toString());
        if (error != null) {
            if (error instanceof TimeoutError) {
                serverResponseListener.onRequestError(context.getString(R.string.error_timeout), listener);
            } else if (error instanceof NoConnectionError) {
                serverResponseListener.onRequestError(context.getString(R.string.error_connection), listener);
            } else if (error instanceof ServerError || error instanceof NetworkError) {
                serverResponseListener.onRequestError(context.getString(R.string.error_network), listener);
            } else if (error instanceof ParseError) {
                serverResponseListener.onRequestError(context.getString(R.string.error_parse), listener);
            } else {
                serverResponseListener.onRequestError(context.getString(R.string.error_general), listener);
            }
        }

    }
}
