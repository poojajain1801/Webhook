package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tanmay.Patel on 1/20/2017.
 */
@Component
public class HttpClintImpl implements HttpClint {

    @Autowired
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(HCEControllerSupport.class);
    public String postHttpRequest(byte[] requestData, String url) {
        int responseCode = -1;
        String responseBody = null;
        try {

            String proxyip = env.getProperty("proxyip");
            String proxyport = env.getProperty("proxyport");
            String username = env.getProperty("username");
            String password = env.getProperty("password");

            System.setProperty("http.proxyHost",proxyip );
            System.setProperty("http.proxyPort", proxyport);
            System.setProperty("http.proxyUser", username);
            System.setProperty("http.proxyPassword", password);

            System.setProperty("https.proxyHost", proxyip);
            System.setProperty("https.proxyPort", proxyport);
            System.setProperty("https.proxyUser", username);
            System.setProperty("https.proxyPassword", password);

            System.out.println("Sending FCM request");
            byte[] postData = (requestData);

            URL urlObj = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlObj.openConnection();

            //set timeputs to 10 seconds
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));

            OutputStream out = httpURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpURLConnection.getResponseCode();
            //success
            if (responseCode == HttpStatus.SC_OK) {
                responseBody = convertStreamToString(httpURLConnection.getInputStream());
                System.out.println("FCM message sent : " + responseBody);
            } else {
                //failure
                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
                //System.out.println("Sending FCM request failed for regId: " + deviceRegistrationId + " response: " + responseBody);
            }
        } catch (IOException ioe) {
            //System.out.println("IO Exception in sending FCM request. regId: " + deviceRegistrationId);
            LOGGER.error("Exception Occured" +ioe);

        } catch (Exception e) {
            //System.out.println("Unknown exception in sending FCM request. regId: " + deviceRegistrationId);
            LOGGER.error("Exception Occured" +e);
        }
        return responseBody;
    }

    public String convertStreamToString(InputStream inStream) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

}
