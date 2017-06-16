package com.comviva.mfs.promotion.util.rns.fcm;

import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 *
 */
public class FCMMessageSender {
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_API_KEY    = "AAAAjxiVr-o:APA91bHPswyHutJAK1qBAYfjXctQu9n-Y8woGt_HTZZbbd8M0-yLVAVEyMMnqMu3_9WNCXrdV_HNG5ra0sEe6EYUMXj52RruhOb8PDwa_id5goRBKuGJSEktQ-CmNV3d4LdPfmcAiYpo";
    private static final String deviceRegistrationId =  "cUgsdrgkF8g:APA91bE4gDnWSZPlR64xlPr0u1eDNlljhRzTTmd-WQo9bE2k3i4rfO3qVq6f_rNW5CZYuAQMZQELImB8ofYMM5y-QpUYsXK15lIw2kGMZm7LqlqYaxJelKSdbcjczF9k8CIX1TgWAFWN";

    public static void main(String args[])
    {
        int responseCode = -1;
        String responseBody = null;
        try
        {
            System.setProperty("http.proxyHost", "172.19.7.180");
            System.setProperty("http.proxyPort", "8080");
            System.setProperty("http.proxyUser", "tarkeshwar.v");
            System.setProperty("http.proxyPassword", "dec.2016");

            System.setProperty("https.proxyHost", "172.19.7.180");
            System.setProperty("https.proxyPort", "8080");
            System.setProperty("https.proxyUser", "tarkeshwar.v");
            System.setProperty("https.proxyPassword", "dec.2016");

            System.out.println("Sending FCM request");
            byte[] postData = getPostData(deviceRegistrationId);

            URL url = new URL(FCM_URL);
            HttpsURLConnection httpURLConnection = (HttpsURLConnection)url.openConnection();

            //set timeputs to 10 seconds
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            httpURLConnection.setRequestProperty("Authorization", "key="+FCM_SERVER_API_KEY);

            OutputStream out = httpURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpURLConnection.getResponseCode();
            //success
            if (responseCode == HttpStatus.SC_OK)
            {
                responseBody = convertStreamToString(httpURLConnection.getInputStream());
                System.out.println("FCM message sent : " + responseBody);
            }
            //failure
            else
            {
                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
                System.out.println("Sending FCM request failed for regId: " + deviceRegistrationId + " response: " + responseBody);
            }
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception in sending FCM request. regId: " + deviceRegistrationId);
            ioe.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println("Unknown exception in sending FCM request. regId: " + deviceRegistrationId);
            e.printStackTrace();
        }
    }

    public static byte[] getPostData(String registrationId) throws JSONException {
        HashMap<String, String> dataMap = new HashMap<>();
        JSONObject payloadObject = new JSONObject();

        dataMap.put("name", "tarke!");
        dataMap.put("country", "Banarasiya");

        JSONObject data = new JSONObject(dataMap);;
        payloadObject.put("data", data);
        payloadObject.put("to", registrationId);

        return payloadObject.toString().getBytes();
    }

    public static String convertStreamToString (InputStream inStream) throws Exception
    {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bReader.readLine()) != null)
        {
            sb.append(line);
        }

        return sb.toString();
    }

}