package com.comviva.mfs.promotion.util.httpHandler;


import com.newrelic.agent.deps.org.apache.http.HttpStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tanmay.Patel on 1/20/2017.
 */
public class HttpClint {
    public static final int TIMEOUT = 20*1000;
    public String postHttpRequest(byte[] requestData, String url) {
        int responseCode = -1;
        String responseBody = null;
        try {
            System.setProperty("http.proxyHost", "172.19.7.180");
            System.setProperty("http.proxyPort", "8080");
            System.setProperty("http.proxyUser", "tarkeshwar.v");
            System.setProperty("http.proxyPassword", "dec.2016");

            System.setProperty("https.proxyHost", "172.19.7.180");
            System.setProperty("https.proxyPort", "8080");
            System.setProperty("https.proxyUser", "tarkeshwar.v");
            System.setProperty("https.proxyPassword", "dec.2016");

            System.out.println("Sending FCM request");
            byte[] postData = requestData;

            URL urlObj = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlObj.openConnection();

            // Set timeout to 10 seconds
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);

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
            } else { //failure
                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public String convertStreamToString(InputStream inStream) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

}
