package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

import com.newrelic.agent.deps.org.apache.http.HttpStatus;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

/**
 * Implementation of RemoteNotification interface.
 * This implementation use FCM(Firebase Cloud Messaging).
 * Created by tarkeshwar.v on 2/14/2017.
 */
public class FcmRns implements RemoteNotification {
    /** URL of FCM server */
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    protected FcmRns() {
    }

    private String convertStreamToString (InputStream inStream) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    public RnsResponse sendRns(byte[] rnsPostData) {
        int responseCode;
        String responseBody;
        RnsResponse response = new RnsResponse();

        try {
            // Proxy Setting
            System.setProperty("http.proxyHost", "172.19.7.180");
            System.setProperty("http.proxyPort", "8080");
            System.setProperty("http.proxyUser", "tarkeshwar.v");
            System.setProperty("http.proxyPassword", "may.2017");

            System.setProperty("https.proxyHost", "172.19.7.180");
            System.setProperty("https.proxyPort", "8080");
            System.setProperty("https.proxyUser", "tarkeshwar.v");
            System.setProperty("https.proxyPassword", "may.2017");

            URL url = new URL(FCM_URL);
            HttpsURLConnection httpURLConnection = (HttpsURLConnection)url.openConnection();

            //Set timeout to 10 seconds
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(rnsPostData.length));
            httpURLConnection.setRequestProperty("Authorization", "key=" + SERVER_KEY);

            OutputStream out = httpURLConnection.getOutputStream();
            out.write(rnsPostData);
            out.close();
            responseCode = httpURLConnection.getResponseCode();

            // Notification sent successfully
            if (responseCode == HttpStatus.SC_OK) {
                responseBody = convertStreamToString(httpURLConnection.getInputStream());
            } else {
                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
            }

            response.setErrorCode(Integer.toString(responseCode));
            response.setResponse(responseBody);
        } catch (Exception ioe) {
//            response.setErrorCode(Integer.toString(ConstantErrorCodes.INTERNAL_SERVICE_FAILURE));
//            response.setResponse(ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.INTERNAL_SERVICE_FAILURE));
        }
        return response;
    }

}
