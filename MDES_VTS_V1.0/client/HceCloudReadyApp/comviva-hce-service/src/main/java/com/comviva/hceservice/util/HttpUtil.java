package com.comviva.hceservice.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.mastercard.mcbp.utils.http.HttpGetRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Contains utility method to send request to server.
 * Created by tarkeshwa r.v on 3/3/2017.
 */
public class HttpUtil {
    private static HttpUtil httpUtil;
    private HttpResponse httpResponse;

    public static final int TIMEOUT = 60*1000;

    private HttpUtil() {
    }

    private void setProxy() {
        System.setProperty("http.proxyHost", "172.19.7.180");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("http.proxyUser", "tarkeshwar.v");
        System.setProperty("http.proxyPassword", "feb.2017");

        System.setProperty("https.proxyHost", "172.19.7.180");
        System.setProperty("https.proxyPort", "8080");
        System.setProperty("https.proxyUser", "tarkeshwar.v");
        System.setProperty("https.proxyPassword", "feb.2017");
    }

    private String convertStreamToString(InputStream inStream) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static HttpUtil getInstance() {
        if(null == httpUtil) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public HttpResponse postRequest(String serviceUrl, String requestData) {
        //setProxy();
        httpResponse = new HttpResponse();

        HttpURLConnection httpURLConnection = null;
        OutputStream out = null;
        InputStream in = null;

        int responseCode;
        String responseData;
        try {
            byte[] postData = requestData.getBytes();

            URL url = new URL(serviceUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));

            out = httpURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpURLConnection.getResponseCode();
            httpResponse.setStatusCode(responseCode);

            in = httpURLConnection.getInputStream();
            if (responseCode == 200) {
                responseData = convertStreamToString(in);
            } else {
                responseData = convertStreamToString(in);
            }
            httpResponse.setResponse(responseData);
        } catch (IOException ioe) {
            httpResponse.setReqStatus("Server is not responding");
        } catch (Exception e) {
            httpResponse.setReqStatus("Unknown exception");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if(null != out) {
                    out.close();
                }
                if(null != httpURLConnection) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException ioe) {
            }
        }
        return httpResponse;
    }

    public HttpResponse getRequest(String serviceUrl, Map<String, String> queryStrings) {
        //setProxy();
        httpResponse = new HttpResponse();

        HttpURLConnection httpURLConnection = null;
        InputStream in = null;

        int responseCode;
        String responseData;
        try {
            // Prepare Query String
            if(queryStrings.size() > 0) {
                String strQueries = "";
                boolean isFirstElement =  true;
                for (String key : queryStrings.keySet()) {
                    if(isFirstElement) {
                        strQueries += "?" + key + "=" + queryStrings.get(key);
                        isFirstElement = false;
                    } else {
                        strQueries += "&" + key + "=" + queryStrings.get(key);
                    }
                }
                serviceUrl += strQueries;
            }

            URL url = new URL(serviceUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.connect();
            responseCode = httpURLConnection.getResponseCode();
            httpResponse.setStatusCode(responseCode);

            in = httpURLConnection.getInputStream();
            if (responseCode == 200) {
                responseData = convertStreamToString(in);
            } else {
                responseData = convertStreamToString(in);
            }
            httpResponse.setResponse(responseData);
        } catch (IOException ioe) {
            httpResponse.setReqStatus("Server is not responding");
        } catch (Exception e) {
            httpResponse.setReqStatus("Unknown exception");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if(null != httpURLConnection) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException ioe) {
            }
        }
        return httpResponse;
    }
}


