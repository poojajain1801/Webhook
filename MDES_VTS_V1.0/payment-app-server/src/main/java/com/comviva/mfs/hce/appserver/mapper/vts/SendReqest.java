package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SendReqest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendReqest.class);
    public String postHttpRequest(byte[] requestData, String url, JSONObject header) {

        int responseCode = -1;
        String responseBody = null;
        try {
            System.setProperty("http.proxyHost", "172.19.1.240");
            System.setProperty("http.proxyPort", "3128");
            System.setProperty("http.proxyUser", "tanmay.patel");
            System.setProperty("http.proxyPassword", "them0ther@@");
            System.setProperty("https.proxyHost", "172.19.1.240");
            System.setProperty("https.proxyPort", "3128");
            System.setProperty("https.proxyUser", "tanmay.patel");
            System.setProperty("https.proxyPassword", "them0ther@@");


            byte[] postData = requestData;

            URL urlObj = new URL(url);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)urlObj.openConnection();

            //set timeputs to 10 seconds
            httpsURLConnection.setConnectTimeout(10000);
            httpsURLConnection.setReadTimeout(10000);
            //headers.add("Accept-Encoding","deflate");
            //headers.add("Host","sandbox.digital.visa.com");
            // headers.add("Connection","Keep-Alive");
            //headers.add("User-Agent", "Apache-HttpClient/4.1.1");
            String xpaytoken =  generateXPayToken(header);
            String requestId = (String) header.get("xRequestId");

            LOGGER.debug("url =:"+url);
            LOGGER.debug("xpaytoken = "+xpaytoken);
            LOGGER.debug("requestId = "+requestId);

            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setRequestMethod("PUT");
            httpsURLConnection.setRequestProperty("Content-Type", "application/json");
            httpsURLConnection.setRequestProperty("Accept", "application/json");
            httpsURLConnection.setRequestProperty("Accept-Charset","UTF-8");
            httpsURLConnection.setRequestProperty("Accept-Encoding","deflate");
            httpsURLConnection.setRequestProperty("Host","sandbox.digital.visa.com");
            httpsURLConnection.setRequestProperty("Connection","Keep-Alive");
            httpsURLConnection.setRequestProperty("User-Agent", "Apache-HttpClient/4.1.1");
         //   httpsURLConnection.setRequestProperty("Content-Language", "en-US");
         //   httpsURLConnection.setRequestProperty("X-Content-Type-Options", "nosniff");
            httpsURLConnection.setRequestProperty("x-request-id", requestId);
            httpsURLConnection.setRequestProperty("x-pay-token", xpaytoken);
            httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(header.get("requestBody").toString().getBytes("UTF-8").length));

            OutputStream out = httpsURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpsURLConnection.getResponseCode();
            //success
            if (responseCode == HttpStatus.SC_OK) {
                responseBody = convertStreamToString(httpsURLConnection.getInputStream());
                Map<String, List<String>> responseheader = httpsURLConnection.getHeaderFields();
                String xCorrelationID = responseheader.get("X-CORRELATION-ID").get(0);
                LOGGER.debug("Enroll device https response xCorrelationID = " + xCorrelationID);
                LOGGER.debug("Enroll device https response = " + responseBody);
            } else {
                //failure
                responseBody = convertStreamToString(httpsURLConnection.getErrorStream());
                Map<String, List<String>> responseheader = httpsURLConnection.getHeaderFields();
                String xCorrelationID = responseheader.get("X-CORRELATION-ID").get(0);
                LOGGER.debug("Enroll device https response xCorrelationID = " + xCorrelationID);
                LOGGER.debug("Enroll device https response = " + responseBody);
                //System.out.println("Sending FCM request failed for regId: " + deviceRegistrationId + " response: " + responseBody);
            }
        } catch (IOException ioe) {
            //System.out.println("IO Exception in sending FCM request. regId: " + deviceRegistrationId);
            ioe.printStackTrace();
        } catch (Exception e) {
            //System.out.println("Unknown exception in sending FCM request. regId: " + deviceRegistrationId);
            e.printStackTrace();
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

    protected String generateXPayToken(JSONObject prepareHeaderRequest) {
        String hmacSha256 ="";
        String sharedSecret = "SldL{6-ruzhvj1}gCIaTgIpb5O#fU@qnEv#is+t2";
        byte[] bsharedSecret = sharedSecret.getBytes();
        // JSONObject object=new JSONObject(prepareHeaderRequest.get("requestBody"));
        long utcTimestamp = System.currentTimeMillis() / 1000L;
        String xPayToken = "xv2:" + utcTimestamp + ":";
        //String query_string = "apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc";
        //String resource_path="vts/clients/vClientID/devices/clientDeviceID";
        //String hashInput = (utcTimestamp+resource_path+query_string+object.toString());
        String hashInput = (utcTimestamp+(String)prepareHeaderRequest.get("resourcePath")+prepareHeaderRequest.get("queryString")+prepareHeaderRequest.get("requestBody"));
        System.out.println("hashInput:"+hashInput);
        try {
            byte[] bHmacSha256 = MessageDigestUtil.hMacSha256(hashInput.getBytes("UTF-8"),bsharedSecret);
            hmacSha256 = ArrayUtil.getHexString(bHmacSha256).toLowerCase();
            System.out.println("bHmacSha256 in byte :   "+bHmacSha256);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("hmacSha256:    "+hmacSha256);
        System.out.println("X-PAY-TOKEN IS :   "+xPayToken + hmacSha256);
        return xPayToken + hmacSha256;
    }

}
