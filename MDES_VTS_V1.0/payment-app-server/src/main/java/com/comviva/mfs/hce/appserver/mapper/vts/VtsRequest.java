package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VtsRequest {
    protected static final String PATH_SEPARATOR = "/";

    @Autowired
    protected Environment env;

    /**
     * Base URL of VTS Sandbox or live
     */
    protected String vtsUrl;
    /**
     * Client-specific API key issued during on-boarding.
     */
    protected String apiKey;
    /**
     * shared_secret is the private key value associated with your API key
     */
    protected byte[] sharedSecret;
    /**
     * Query Parameters
     */
    protected StringBuilder queryString;
    /**
     * Request Header
     */
    protected HttpHeaders headers;

    /**
     * Unique ID for the API request.
     * Format: String. Size: 1-36, [A-Z][a-z][0-9,-]
     */
    protected String xRequestId;

    /**
     * Request in JSON format
     */
    protected JSONObject jsonRequest;

    public VtsRequest(Environment env) {
        this.env = env;
        vtsUrl = env.getProperty("visaBaseUrlSandbox");
        apiKey = env.getProperty("apiKey");
        sharedSecret = env.getProperty("sharedSecret").getBytes(Charset.forName("UTF-8"));
        queryString = new StringBuilder();
        jsonRequest = new JSONObject();

        // Initializing Header
        headers = new HttpHeaders();
        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);
        headers.setContentType(MediaType.APPLICATION_JSON);

        //headers.add("Accept", "application/json");
        //headers.add("Content-Type", "application/json");
        //headers.add("Accept-Charset","UTF8");
        //headers.add("Accept-Encoding","deflate");
        //headers.add("Host","sandbox.digital.visa.com");
        //headers.add("Connection","Keep-Alive");
        //headers.add("User-Agent", "Apache-HttpClient/4.1.1");
    }

    protected void prepareHeader(JSONObject prepareHeaderRequest) {
        headers.add("x-request-id", (String) prepareHeaderRequest.get("xRequestId"));
        headers.add("x-pay-token", generateXPayToken(prepareHeaderRequest));
        try {
         //   headers.add("Content-Length",String.valueOf(requestBody.getBytes("UTF-8").length));
            //String contentlength = String.valueOf(prepareHeaderRequest.getString("requestBody").length());
           // headers.add("Content-Length",contentlength);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected String generateXPayToken(JSONObject prepareHeaderRequest) {
        String hmacSha256 ="";
       // JSONObject object=new JSONObject(prepareHeaderRequest.get("requestBody"));

/*        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

        Date date = Date.from(utc.toInstant());
        long utcTimestamp = utc.toEpochSecond();*/

       long utcTimestamp = System.currentTimeMillis() / 1000L;
        String xPayToken = "xv2:" + utcTimestamp + ":";
        //String query_string = "apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc";
        //String resource_path="vts/clients/vClientID/devices/clientDeviceID";
        //String hashInput = (utcTimestamp+resource_path+query_string+object.toString());
        String hashInput = (utcTimestamp+(String)prepareHeaderRequest.get("resourcePath")+prepareHeaderRequest.get("queryString")+prepareHeaderRequest.get("requestBody"));
        System.out.println("hashInput:"+hashInput);
        try {
            byte[] bHmacSha256 = MessageDigestUtil.hMacSha256(hashInput.getBytes("UTF-8"),sharedSecret);
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
