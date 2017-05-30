package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.net.*;

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
        //reqBody ="{ \"deviceInfo\": {   \"osType\": \"ANDROID\",   \"deviceType\": \"PHONE\",   \"deviceName\": \"Mydevice\" }, \"channelSecurityContext\": {   \"deviceCerts\": [    {     \"certUsage\": \"CONFIDENTIALITY\",     \"certFormat\": \"X509\",     \"certValue\": \"-----BEGIN CERTIFICATE-----MIIDtzCCAp+gAwIBAgIBAjANBgkqhkiG9w0BAQsFADCBnjELMAkGA1UEBhMCSU4xEjAQBgNVBAgMCUtBUk5BVEFLQTESMBAGA1UEBwwJQkFOR0FMT1JFMRAwDgYDVQQKDAdDT01WSVZBMQwwCgYDVQQLDANNRlMxFjAUBgNVBAMMDUhDRUNMT1VEUkVBRFkxLzAtBgkqhkiG9w0BCQEWIHRhcmtlc2h3YXIudkBtYWhpbmRyYWNvbXZpdmEuY29tMB4XDTE3MDUxOTA1NDMxMFoXDTE5MDUxOTA1NDMxMFowgZ4xCzAJBgNVBAYTAklOMRIwEAYDVQQIDAlLQVJOQVRBS0ExEjAQBgNVBAcMCUJBTkdBTE9SRTEQMA4GA1UECgwHQ09NVklWQTEMMAoGA1UECwwDTUZTMRYwFAYDVQQDDA1IQ0VDTE9VRFJFQURZMS8wLQYJKoZIhvcNAQkBFiB0YXJrZXNod2FyLnZAbWFoaW5kcmFjb212aXZhLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJ9ArmQ38D19BVf4C9nCcKb91poBB77KN6Nvm2++E4lIAHFDOE1kTkRlJKD4rMbQVjUPZIa1qH2I7sJnWXK6hgLVFTZG+VZpgEpyg1e7mi2gmjGe6HD9uk3p8tMY7BMe/e/XNE42X+4RSGLvAEBmXtM9Zfq3A71PzhDCoirjxkv/QzPF8DGoGOMNKV7zSrxjYCpKkA2c5IPbaD4Q6JNtoP0D2roaaC66mOSNbQm3KPXNvsb9h9Xca0njlngnZMbNQ+XOiRcBh3ce2kwUvdpr8nBXCluUjiS1Qa0Uu8QhNY+AkYkrWoCWKlnzHwN2n1wjaimwmTx7LvtacDbLIOwQgZECAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAJGMUEOv3s1EZ+LCCqjF+E6dGr/Z9M7RZXm5nlvsbCFZnngGtA8jx/Qj97olX9542krkFKR/D6jcjCOZmG10hiUZeu0OXFbQ5moH4t3Z9Vnalf2HUZUIVfdE3l8QIsqVelmD7cfPfHMDNIUM7IX6MSbavX2BU7FqByv+2hbeLlTFXpFC9x6Chmu4zDVRj3lU35iMjIGn/47iQWwayPUnXhrHNoHRkY0aNQ4dfFiwXXqIt2dKYi/3yzbZhk1fU+hh9V8BYivgGrUAEZFE9mC7915dDCg8iMiN90DSiFsqNzRoqpkxq6FCpQtLpKKTof50g8ArVFz0zSyp5z8ZzU/Oxnw==-----END CERTIFICATE-----\"    },    {     \"certUsage\": \"INTEGRITY\",     \"certFormat\": \"X509\",     \"certValue\": \"-----BEGIN CERTIFICATE-----MIIDtzCCAp+gAwIBAgIBAjANBgkqhkiG9w0BAQsFADCBnjELMAkGA1UEBhMCSU4xEjAQBgNVBAgMCUtBUk5BVEFLQTESMBAGA1UEBwwJQkFOR0FMT1JFMRAwDgYDVQQKDAdDT01WSVZBMQwwCgYDVQQLDANNRlMxFjAUBgNVBAMMDUhDRUNMT1VEUkVBRFkxLzAtBgkqhkiG9w0BCQEWIHRhcmtlc2h3YXIudkBtYWhpbmRyYWNvbXZpdmEuY29tMB4XDTE3MDUxOTA1NDMxMFoXDTE5MDUxOTA1NDMxMFowgZ4xCzAJBgNVBAYTAklOMRIwEAYDVQQIDAlLQVJOQVRBS0ExEjAQBgNVBAcMCUJBTkdBTE9SRTEQMA4GA1UECgwHQ09NVklWQTEMMAoGA1UECwwDTUZTMRYwFAYDVQQDDA1IQ0VDTE9VRFJFQURZMS8wLQYJKoZIhvcNAQkBFiB0YXJrZXNod2FyLnZAbWFoaW5kcmFjb212aXZhLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALKR3z+BXyIEHGtI/n5nuJdPuNqMmwY0/KNkR2niI1aeWfkIZwSQGgEwBcMBc+ywXlRI7R4VWw0wun+gSNjsS0gQ3zKVMGWdbA/ilvLqoNUg7ce3rw8mb2vdBu3d97+ns4bY1B2B2yd/B5rAIsnf7iM7frkNsowhOf3nZDqtiCMrUlKP80DIfp8zkHp34SwR7nQ+caQNiyAZd2WAZvVLztbzC8QeNauXK+sLnMq3P3cIv0EyDg9J67qqzPd7lRR/kOqukz4ZA1Y1cYBE7oicC0PbSZkTVMBv4R5ev2DHxyQX1VHzdy0gAZQhB9S5PEYuzBlx+SWmWrNLuo9Ao/nzBe8CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAaKqAO3AFk1eT58+VMNUZIW6DMYfzbZD+Nryvf8vcWukERcOId41a0fwG995fj+o0nVOJOcqHFyV7t0qzbJDqZDbOylwFJ05Y0CMyXJ25IpIilJJ0D+ntGFYjvKJ+esKboLF5dwWbxYbRY9xCyprzqJhhspzH3J4nqdS9e726O1Yzpg7Riu8sMPq6O6Q66tAPrtghUunEBA8v/WJOQc7iSKq4cy5u9NVKtoAOZb6BTvoNkIVL89kXiYas7AfBCRUujWa/3rytRbbFULpuyvhnQ2pr4c3tNSg4bzhrY5zGvcNoW07XkueUzTvxRBnlFYr3GrLOA/n9t238XLCLyBnmxw==-----END CERTIFICATE-----\"    },    {     \"certUsage\": \"DEVICE_ROOT\",     \"certFormat\": \"X509\",     \"certValue\": \"-----BEGIN CERTIFICATE-----MIIDtzCCAp+gAwIBAgIBAjANBgkqhkiG9w0BAQsFADCBnjELMAkGA1UEBhMCSU4xEjAQBgNVBAgMCUtBUk5BVEFLQTESMBAGA1UEBwwJQkFOR0FMT1JFMRAwDgYDVQQKDAdDT01WSVZBMQwwCgYDVQQLDANNRlMxFjAUBgNVBAMMDUhDRUNMT1VEUkVBRFkxLzAtBgkqhkiG9w0BCQEWIHRhcmtlc2h3YXIudkBtYWhpbmRyYWNvbXZpdmEuY29tMB4XDTE3MDUxOTA1NDMxMFoXDTE5MDUxOTA1NDMxMFowgZ4xCzAJBgNVBAYTAklOMRIwEAYDVQQIDAlLQVJOQVRBS0ExEjAQBgNVBAcMCUJBTkdBTE9SRTEQMA4GA1UECgwHQ09NVklWQTEMMAoGA1UECwwDTUZTMRYwFAYDVQQDDA1IQ0VDTE9VRFJFQURZMS8wLQYJKoZIhvcNAQkBFiB0YXJrZXNod2FyLnZAbWFoaW5kcmFjb212aXZhLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK0cS3rQtamZu3d103OiJkHLCNGu4A3mtjZbzfcTVzpKVeLJP4QTkXZNvhpQDHPP+jexdZ+7mTUSYqcW4JsAvQtUcTYDsTvmmw8j6T1KFFjmTPm4Wcziz50agUD44t6tfyvtL/SjGRuFyLomxP4FHZbzSDu9ETGitPILEVPnza+4mdnYfe11A7M5oge6FP9lwpAjp1p+xtG6tueXwAvRdhNS3yyVZcJ2qju41lRkFtrGd189DHZidFQzZ5mu5OM37iTljSf0Jya1K9g4clZZ5cN/ioq+M6GitnZ1Sigtv44L6vtAXehZVcQW0W2lsdKsBxD7qurgfgs5O/fYiwpTMYECAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAMxoQLUfB555FYKF8gbhnQ+8IInDf3WFCEnqt0EIINggr0ZTnWzqF5eAf0VYc69L66FI0ePKyxijzmW4ZfI36AQqwefZoQaj+550yx7bv8i2o1swi/LwPTFCZzhf27dPUt2wFKwybMpdCpVAo5LuFXHz13uqsEWsOwGHjisbCIG/2MDclYwh2ml2DIDlFfEHElErqWbNTgru3G1UsNs9c+M3RRcp6jVE21waee6hvAFlpVEB401Wslyj1XQiQDzz36lxm8c+2f2UetYhT2vvhZiXLICismt9Uj1x1s+TvZqOdgkxgtdyqLLTUGqZYm7KM638AHKRnWsYCR0JI0BOvVA==-----END CERTIFICATE-----\"    }   ],   \"vtsCerts\": [    {     \"certUsage\": \"CONFIDENTIALITY\",     \"vCertificateID\": \"f1606e98\"    },    {     \"certUsage\": \"INTEGRITY\",     \"vCertificateID\": \"bf617210\"    }   ],   \"channelInfo\": {    \"encryptionScheme\": \"RSA_PKI\"   } } }";
        JSONObject object=new JSONObject(reqBody);

        long utcTimestamp = System.currentTimeMillis() / 1000L;
       String xPayToken = "xv2:" + utcTimestamp + ":";
        //String xPayToken = "xv2:" + 1496034624 + ":";
        String query_string = "apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc";
        String encoded_query_string  = null;
        try {
            encoded_query_string  = URLEncoder.encode(query_string , "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("encoded_query_string:     "+encoded_query_string);
        String hmacSha256 ="";
        String resource_path="vts/clients/ff1b95f4-06d9-b032-00c1-178ec0fd7201/devices/00015C1BACA69DDD65DE0964";
        System.out.println("resource_path:   "+resource_path);
        URI uri=null;
            try {
                uri = new URI(resource_path);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            System.out.println(uri);
            System.out.println(uri.toString());
        String hashInput = (utcTimestamp+uri.toString()+encoded_query_string+object.toString());
        System.out.println("hashInput:   "+hashInput);
        try {
            byte[] bHmacSha256 = MessageDigestUtil.hMacSha256(hashInput.getBytes("UTF-8"),sharedSecret);
            hmacSha256 = ArrayUtil.getHexString(bHmacSha256);
            System.out.println("bHmacSha256 in byte :   "+bHmacSha256);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("hmacSha256:    "+hmacSha256);
        System.out.println("X-PAY-TOKEN IS :   "+xPayToken + hmacSha256);
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
