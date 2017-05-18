package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

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
        sharedSecret = env.getProperty("sharedSecret").getBytes();
        queryString = new StringBuilder();
        jsonRequest = new JSONObject();

        // Initializing Header
        headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
    }

    protected void prepareHeader(String xRequestId, RequestId requestId, String queryString, String requestBody) {
        headers.add("x-request-id", xRequestId);
        headers.add("x-pay-token", generateXPayToken(requestId.getResourcePath(), queryString, requestBody));
    }

    /**
     * Generates x-pay-token to be sent in header.
     *
     * @param resourcePath Resource path (API name)
     * @param queryString  This HTTPS request's query string
     * @param reqBody      Complete request body, when a request body exists
     * @return x-pay-token
     */
    protected String generateXPayToken(String resourcePath, String queryString, String reqBody) {
        // UTC_Timestamp is a UNIX Epoch timestamp, in seconds
        long utcTimestamp = System.currentTimeMillis() / 1000L;
        String xPayToken = "x-pay-token: xv2:" + utcTimestamp + ":";

        // Calculating HMAC-SHA256_hash
        String hmacSha256 = "";
        String hashInput = utcTimestamp + resourcePath + (queryString != null ? queryString : "") + (reqBody != null ? reqBody : "");
        try {
            byte[] bHmacSha256 = MessageDigestUtil.hMacSha256(hashInput.getBytes("UTF-8"), sharedSecret);
            hmacSha256 = ArrayUtil.getHexString(bHmacSha256);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xPayToken + hmacSha256;
    }

    /**
     * Appends query string.<br/>
     * Note - apiKey is set by default as it is required by all apis. Append others query parameters only.
     *
     * @param key   Query key
     * @param value Query value
     */
    public void addQueryString(final String key, final String value) {
        queryString.append(queryString.length() == 0 ? (key + "=" + value) : ("&" + key + "=" + value));
    }

    public void reset() {
        queryString.replace(0, queryString.length(), "apiKey=" + apiKey);
        headers.clear();
        jsonRequest = new JSONObject();
    }

}
