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

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amit.randhawa on 29-Aug-17.
 */


public class ActiveAccountManagementService extends Service {
    private static final String TAG = ActiveAccountManagementService.class.getSimpleName();
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
                   /* ReplenishProvider replenishProvider = new ReplenishProviderImpl(vProvisionTokenId);
                    replenishProvider.doRequest(replenishRequestPayload, new ReplenishResponseCallback(vProvisionTokenId,tokenKey));*/
                }
            } catch (TokenInvalidException e) {
                sendNotification(false, tokenKey, e.getMessage());
            }
        }
    }

    private void replenishTokenRequest(final TokenKey tokenKey, ReplenishRequest replenishRequest, final ResponseListener responseListener) {
        final JSONObject replenishTokenRequestObject = new JSONObject();
        try {
            //replenishTokenRequestObject.put(Tags.USER_ID.getTag(),replenishRequest.get)
            //replenishTokenRequestObject.put(Tags.ACTIVATION_CODE.getTag(),replenishRequest.get)
            replenishTokenRequestObject.put(Tags.MAC.getTag(), replenishRequest.getSignature());
            // replenishTokenRequestObject.put(Tags.API.getTag(),replenishRequest.)
            replenishTokenRequestObject.put(Tags.SC.getTag(), replenishRequest.getSignature());
            replenishTokenRequestObject.put(Tags.TV1.getTag(), replenishRequest.getTvls());
            replenishTokenRequestObject.put(Tags.ENCRYPTION_META_DATA.getTag(), replenishRequest.getEncryptionMetaData());

        } catch (Exception e) {
            responseListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }

        class ReplenishTokenRequest extends AsyncTask<Void, Void, HttpResponse> {
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSReplenishTokenUrl(), replenishTokenRequestObject.toString());
            }

            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                try {
                    if (httpResponse.getStatusCode() == 200) {
                        String vProvisionTokenId = visaPaymentSDK.getTokenData(tokenKey).getVProvisionedTokenID();
                        confirmReplenishment(responseListener, tokenKey);
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

    private void confirmReplenishment(final ResponseListener responseListener, TokenKey tokenKey) {
        ReplenishAckRequest replenishAckRequest = visaPaymentSDK.constructReplenishAcknowledgementRequest(tokenKey);
        final JSONObject confirmReplenishTokenRequestObject = new JSONObject();
        try {
            // confirmReplenishTokenRequestObject.put(Tags.USER_ID.getTag(),replenishAckRequest.get)
            // confirmReplenishTokenRequestObject.put(Tags.ACTIVATION_CODE.getTag(),replenishRequest.get);
            confirmReplenishTokenRequestObject.put(Tags.TOKEN_INFO.getTag(), replenishAckRequest.getTokenInfo());
        } catch (Exception e) {
            responseListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        }

        class ConfirmReplenishmentRequest extends AsyncTask<Void, Void, HttpResponse> {
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getVTSConfirmReplenishTokenUrl(), confirmReplenishTokenRequestObject.toString());
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
            // walletApplication = (WalletApplication) getApplicationContext();
            visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            //  visaPaymentSDK = walletApplication.getVisaPaymentSDK();
            // commonService = RestAdapterManager.getInstance().getCommonService();
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