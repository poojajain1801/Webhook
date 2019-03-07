package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

import com.comviva.mfs.hce.appserver.service.RemoteNotificationServiceImpl;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

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
    private Environment env;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNotificationServiceImpl.class);
    protected FcmRns(Environment env) {

        this.env= env;
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
            if(env.getProperty("is.proxy.required").equals("Y")) {
                String proxyip = env.getProperty("proxyip");
                String proxyport = env.getProperty("proxyport");
                String username = env.getProperty("username");
                String password = env.getProperty("password");

                System.setProperty("http.proxyHost", proxyip);
                System.setProperty("http.proxyPort", proxyport);
                System.setProperty("http.proxyUser", username);
                System.setProperty("http.proxyPassword", password);
                System.setProperty("https.proxyHost", proxyip);
                System.setProperty("https.proxyPort", proxyport);
                System.setProperty("https.proxyUser", username);
                System.setProperty("https.proxyPassword",password);
            }

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

            LOGGER.debug("FCM server key = "+env.getProperty("serverkey"));

            httpURLConnection.setRequestProperty("Authorization", "key=" +env.getProperty("serverkey"));

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
            LOGGER.error("Exception occured",ioe);
//            response.setErrorCode(Integer.toString(ConstantErrorCodes.INTERNAL_SERVICE_FAILURE));
//            response.setResponse(ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.INTERNAL_SERVICE_FAILURE))
        }
        return response;
    }

}
