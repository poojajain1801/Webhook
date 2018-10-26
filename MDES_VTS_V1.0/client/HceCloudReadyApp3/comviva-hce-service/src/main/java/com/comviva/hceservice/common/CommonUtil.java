package com.comviva.hceservice.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.listeners.CheckCardEligibilityListener;
import com.comviva.hceservice.listeners.DigitizationListener;
import com.comviva.hceservice.listeners.GetAssetListener;
import com.comviva.hceservice.listeners.ResponseListener;
import com.comviva.hceservice.listeners.TransactionHistoryListener;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.crypto.MessageDigestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Locale;

import static com.visa.cbp.sdk.facade.util.ContextHelper.getApplicationContext;

public class CommonUtil {

    private static SDKData sdkData ;


    public static byte[] sha256(final byte[] data) {

        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.toString());
        }
    }


    public static byte[] getDeviceFingerprint(byte[] baDeviceInfo) throws SdkException {

        byte[] deviceFingerprint;
        try {
            deviceFingerprint = MessageDigestUtil.getMessageDigest(baDeviceInfo, MessageDigestUtil.Algorithm.SHA_256);
        } catch (NoSuchAlgorithmException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
        }
        return deviceFingerprint;
    }

    public static CardState getCardStatusFromString(String s){
        if(s.equals(Constants.ACTIVE)){
            return CardState.ACTIVE;
        }else if(s.equals(Constants.SUSPENDED)){
            return CardState.SUSPENDED;
        }else if (s.equals(Constants.MARKED_FOR_DELETION)){
            return CardState.MARKED_FOR_DELETION;
        }else if (s.equals(Constants.INACTIVE)){
            return CardState.INACTIVE;
        }else if (s.equals(Constants.UNKNOWN)){
            return CardState.UNKNOWN;
        }

        return null;
    }


  /*  public static byte[] getBytesFromInputStream(String certificateName) {

        if (certificateName.equalsIgnoreCase("mastercard_public.cer")) {
            InputStream caInput = null;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                caInput = new BufferedInputStream(sdkData.getContext().getAssets().open(certificateName));
                byte[] buffer = new byte[0xFFFF];
                for (int len = caInput.read(buffer); len != -1; len = caInput.read(buffer)) {
                    os.write(buffer, 0, len);
                }
            } catch (IOException e) {
                Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
            } finally {
                if (caInput != null) {
                    try {
                        caInput.close();
                    } catch (IOException e) {
                        Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
                    }
                }
            }
            return os.toByteArray();
        } else {
            return  getBytesFromInputStreamNew(certificateName);
        }
    }*/


    public static byte[] getBytesFromInputStream(String certificateName) {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            String cert = CertificateInStringFormat(getCertificateFromKeystore(certificateName).getEncoded());
            for (int i = 0; i < cert.length(); ++i)
                outStream.write(cert.charAt(i));
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
           Log.e(Tags.ERROR_LOG.getTag(),e.getMessage());
        }
        return outStream.toByteArray();
    }




    private static String CertificateInStringFormat(byte[] data) {

        String cert_begin = "-----BEGIN CERTIFICATE-----\n";
        String end_cert = "-----END CERTIFICATE-----";
        byte[] privateKeyPem = android.util.Base64.encode(data, 0);
        String publicKeyPemStr = new String(privateKeyPem);
        return cert_begin + publicKeyPemStr + end_cert;
    }


    /**
     * Prepares device information in JSON format for MDES.
     *
     * @return Device information in JSON
     */
    public static JSONObject getDeviceInfoInJson() throws SdkException {
        sdkData = SDKData.getInstance();
        JSONObject jsDeviceInfo = new JSONObject();
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) sdkData.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            jsDeviceInfo.put("deviceName", Build.MODEL);
            jsDeviceInfo.put("formFactor", DeviceType.PHONE);
            jsDeviceInfo.put("imei", sdkData.getImei());
            jsDeviceInfo.put("msisdn", mTelephonyMgr.getSubscriberId());
            jsDeviceInfo.put("nfcCapable", (CommonUtil.isNfcEnabled() ? "true" : "false"));
            jsDeviceInfo.put("osName", Constants.OS_NAME);
            jsDeviceInfo.put("osVersion", Build.VERSION.RELEASE);
            jsDeviceInfo.put("serialNumber", ArrayUtil.getHexString(CommonUtil.sha256((Build.FINGERPRINT).getBytes())));
            jsDeviceInfo.put("storageTechnology", "DEVICE_MEMORY");
        } catch (JSONException e) {
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        }
        return jsDeviceInfo;
    }


    public static boolean isNfcEnabled() {
        sdkData = SDKData.getInstance();
        PackageManager pm = sdkData.getContext().getPackageManager();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(sdkData.getContext());
        return pm.hasSystemFeature(PackageManager.FEATURE_NFC) || (null != nfcAdapter);
    }


    /**
     * Identifier for the specific Mobile Payment App instance, unique across a given Wallet Identifier.
     * Maximum length is 48 characters(24 bytes). Random Number (14 bytes) + Current time in millisecond(10 byte)
     *
     * @return PaymentAppInstanceId
     */
    public static String generatePaymentAppInstanceId() {

        byte[] random = ArrayUtil.getRandomNumber(14);
        long currentTimeInMs = new Date().getTime();
        return ArrayUtil.getHexString(random) + String.format(Locale.ENGLISH, "%020d", currentTimeInMs);
    }


    public static String toHex(String arg) {

        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }


    public static PublicKey getPublicKeyFromCert(String certificateName) {
        //InputStream caInput = null;
        CertificateFactory certificateFactory;
        PublicKey pk = null;
        try {
            // caInput = new BufferedInputStream(sdkData.getContext().getAssets().open(certificateName));
            // certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = getCertificateFromKeystore(certificateName);
            pk = certificate.getPublicKey();
        }/* catch (CertificateException | IOException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        }*/ catch (KeyStoreException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        }/* finally {
            if (caInput != null) {
                try {
                    caInput.close();
                } catch (IOException e) {
                    Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
                }
            }
        }*/ catch (CertificateException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        } catch (NoSuchAlgorithmException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        } catch (IOException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        }
        return pk;
    }


    public static byte[] getEncodedFromCert(String certificateName) {
        //InputStream caInput = null;
        CertificateFactory certificateFactory;
        byte[] pk = null;
        try {
            // caInput = new BufferedInputStream(sdkData.getContext().getAssets().open(certificateName));
            // certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = getCertificateFromKeystore(certificateName);
            pk = certificate.getEncoded();
        }/* catch (CertificateException | IOException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        }*/ catch (KeyStoreException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        }/* finally {
            if (caInput != null) {
                try {
                    caInput.close();
                } catch (IOException e) {
                    Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
                }
            }
        }*/ catch (CertificateException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        } catch (NoSuchAlgorithmException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        } catch (IOException e) {
            Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
        }
        return pk;
    }


    public static Certificate getCertificateFromKeystore(String aliasName) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        sdkData = SDKData.getInstance();
        PropertyReader propertyReader = PropertyReader.getInstance(sdkData.getContext());
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        // Provide location of Java Keystore and password for access
        InputStream inputStream = getApplicationContext().getAssets().open(Constants.KEYSTORE_NAME);
        keyStore.load(inputStream, propertyReader.getProperty(PropertyConst.KEY_SDK_KEYSTORE_PASS,PropertyConst.COMVIVA_HCE_CREDENTIALS_FILE).toCharArray());
        Certificate certificate = keyStore.getCertificate(aliasName);
        return certificate;
    }

    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        if(input != null){
            return Base64.encodeToString(input.getBytes(),Base64.DEFAULT);
        }else{
            return null;
        }

    }

    public static String decrypt(String input) {
        if(input != null){
            return new String(Base64.decode(input, Base64.DEFAULT));
        }else{
            return null;
        }

    }


    public static void setSharedPreference(String tag, String data, String sharedPrefName) {
        sdkData = SDKData.getInstance();
        SharedPreferences sharedPreferences = sdkData.getContext().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, data);
        editor.commit();
    }


    public static String getSharedPreference(String tag, String sharedPrefName) {
        sdkData = SDKData.getInstance();
        SharedPreferences sharedPreferences = sdkData.getContext().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(tag, null);
    }


    public static String getUniqueClientDeviceId() {

        int lenRandNoLen = 4;
        byte[] random = ArrayUtil.getRandomNumber(lenRandNoLen);
        long currentTimeInMs = new Date().getTime();
        String clientDeviceId = ArrayUtil.getHexString(random) + String.format(Locale.ENGLISH, "%d", currentTimeInMs);
        return CommonUtil.padData(clientDeviceId, Constants.LEN_CLIENT_DEVICE_ID);
    }


    public static void handleError(Object listener, String... message) {

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
            }else if(listener instanceof TransactionHistoryListener){
                TransactionHistoryListener transactionHistoryListener = (TransactionHistoryListener) listener;
                transactionHistoryListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(Constants.UNKNOWN_RESPONSE_CODE), message[0].toString()));
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
                }else if (listener instanceof TransactionHistoryListener) {
                    TransactionHistoryListener transactionHistoryListener = (TransactionHistoryListener) listener;
                    transactionHistoryListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                }
            }
        }
    }

    public static String padData(String data, int length) {

        final char PAD_CHAR = 'F';
        int paddingSize = length % data.length();
        StringBuilder paddedData = new StringBuilder(data);
        for (int i = 0; i < paddingSize; i++) {
            paddedData.append(PAD_CHAR);
        }
        return paddedData.toString();
    }


    public static void broadcastMessage(Context context, String operation, String cardUniqueID, String status) {

        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_ACTION);
        switch (operation) {
            case Constants.DELETE_USER:
                intent.putExtra(Tags.OPERATION.getTag(), Constants.DELETE_USER);
                break;
            case Constants.TOKEN_STATUS_UPDATED:
                intent.putExtra(Tags.CARD_IDENTIFIER.getTag(), cardUniqueID);
                intent.putExtra(Tags.OPERATION.getTag(), "CARD_STATUS_UPDATE");
                intent.putExtra(Tags.CARD_STATUS.getTag(), status);
                break;
            case Constants.UPDATE_TXN_HISTORY:
                intent.putExtra(Tags.CARD_IDENTIFIER.getTag(), cardUniqueID);
                intent.putExtra(Tags.OPERATION.getTag(), "UPDATE_TXN");
                break;
            case Constants.SUSUPEND_USER:
                intent.putExtra(Tags.OPERATION.getTag(), Constants.SUSUPEND_USER);
                break;
            case Constants.UNSUSPEND_USER:
                intent.putExtra(Tags.OPERATION.getTag(), Constants.UNSUSPEND_USER);
                break;
            default:
                break;
        }
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}



