package com.comviva.hceservice.digitizationApi;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.ResponseListener;
import com.comviva.hceservice.util.UrlUtil;
import com.visa.cbp.external.aam.ReplenishAckRequest;
import com.visa.cbp.external.aam.ReplenishRequest;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.Constants;
import com.visa.cbp.sdk.facade.data.NotificationAction;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service performs replenishment when transaction credential is expired or number of allowed transactions is consumed.
 * Created by amit.randhawa on 29-Aug-17.
 */
public class ActiveAccountManagementService extends Service {
    private VisaPaymentSDK visaPaymentSDK;

    private void callReplenish(final TokenKey tokenKey) {
        if (tokenKey != null) {
            try {
                ReplenishRequest replenishRequestPayload = visaPaymentSDK.constructReplenishRequest(tokenKey);
                replenishRequestPayload.setEncryptionMetaData(null);

                if (replenishRequestPayload != null) {
                    replenishTokenRequest(tokenKey, replenishRequestPayload, new ResponseListener() {
                        @Override
                        public void onStarted() {
                        }

                        @Override
                        public void onSuccess() {
                            sendNotification(true, tokenKey);
                        }

                        @Override
                        public void onError(SdkError sdkError) {
                            sendNotification(false, tokenKey, sdkError.getMessage());
                        }
                    });
                }
            } catch (TokenInvalidException e) {
                sendNotification(false, tokenKey, e.getMessage());
            }
        }
    }

    private void replenishTokenRequest(final TokenKey tokenKey, ReplenishRequest replenishRequest, final ResponseListener responseListener) {
        final String vProvisionTokenId = visaPaymentSDK.getTokenData(tokenKey).getVProvisionedTokenID();
        final JSONObject jsReplenishReq = new JSONObject();
        try {
            jsReplenishReq.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), vProvisionTokenId);
            jsReplenishReq.put(Tags.MAC.getTag(), replenishRequest.getSignature().getMac());
            jsReplenishReq.put(Tags.API.getTag(), replenishRequest.getTokenInfo().getHceData().getDynParams().getApi());
            jsReplenishReq.put(Tags.SC.getTag(), replenishRequest.getTokenInfo().getHceData().getDynParams().getSc());

            JSONArray jsArrTvl = new JSONArray();
            List<String> tvls = replenishRequest.getTvls();
            for (int i = 0; i < tvls.size(); i++) {
                jsArrTvl.put(i, tvls.get(i));
            }
            jsReplenishReq.put("tvl", jsArrTvl);
        } catch (JSONException e) {
            // TODO Error
            return;
        }

        class ReplenishTokenRequest extends AsyncTask<Void, Void, HttpResponse> {
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSReplenishTokenUrl(), jsReplenishReq.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        confirmReplenishment(tokenKey, vProvisionTokenId, responseListener);
                    } else {
                        if (responseListener != null) {
                            responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                        }
                    }
                } catch (Exception e) {
                    if (responseListener != null) {
                        responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                    }
                }
            }
        }
        ReplenishTokenRequest replenishTokenRequest = new ReplenishTokenRequest();
        replenishTokenRequest.execute();
    }

    private void confirmReplenishment(TokenKey tokenKey, final String vProvisionedTokenID, final ResponseListener responseListener) {
        ReplenishAckRequest replenishAckRequest = visaPaymentSDK.constructReplenishAcknowledgementRequest(tokenKey);
        final JSONObject jsConfirmReplenishment = new JSONObject();
        try {
            jsConfirmReplenishment.put(Tags.TOKEN_INFO.getTag(), replenishAckRequest.getTokenInfo());
            jsConfirmReplenishment.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), vProvisionedTokenID);
            jsConfirmReplenishment.put(Tags.API.getTag(), replenishAckRequest.getTokenInfo().getHceData().getDynParams().getApi());
            jsConfirmReplenishment.put(Tags.SC.getTag(), replenishAckRequest.getTokenInfo().getHceData().getDynParams().getSc());
        } catch (Exception e) {
            responseListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }

        class ConfirmReplenishmentRequest extends AsyncTask<Void, Void, HttpResponse> {
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSConfirmReplenishTokenUrl(), jsConfirmReplenishment.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        responseListener.onSuccess();
                    } else {
                        if (responseListener != null) {
                            responseListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                        }
                    }
                } catch (Exception e) {
                    if (responseListener != null) {
                        responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                    }
                }
            }
        }
        ConfirmReplenishmentRequest confirmReplenishmentRequest = new ConfirmReplenishmentRequest();
        confirmReplenishmentRequest.execute();
    }

    private void sendNotification(boolean successful, TokenKey tokenKey) {
        sendNotification(successful, tokenKey, null);
    }

    private void sendNotification(boolean successful, TokenKey tokenKey, String message) {
        Intent intent = new Intent(Constants.VISA_SDK_NOTIFICATION);
        intent.putExtra(Constants.TOKEN_KEY, tokenKey);
        if (successful) {
            intent.putExtra(Constants.VISA_SDK_NOTIFICATION_ACTION, NotificationAction.ACTION_REPLENISH_SUCCESS.getCode());
        } else {
            intent.putExtra(Constants.VISA_SDK_NOTIFICATION_ACTION, NotificationAction.ACTION_REPLENISH_FAIL.getCode());
        }
        if (message != null) {
            intent.putExtra(Constants.TOKEN_STATUS, message);
        }
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (visaPaymentSDK == null) {
            visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        }
        if (intent != null && intent.hasExtra(Constants.REPLENISH_TOKENS_KEY)) {
            ArrayList<TokenKey> tokens = intent.getParcelableArrayListExtra(Constants.REPLENISH_TOKENS_KEY);
            for (final TokenKey tokenKey : tokens) {
                callReplenish(tokenKey);
            }
        }
        return START_NOT_STICKY;
    }
}