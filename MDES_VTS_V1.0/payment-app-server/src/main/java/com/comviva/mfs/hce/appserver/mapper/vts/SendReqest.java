/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Component
public class SendReqest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendReqest.class);
    @Autowired
    public HCEControllerSupport hceControllerSupport;

    @Autowired
    protected Environment env;

    public JSONObject postHttpRequest(byte[] requestData, String request,String url, JSONObject header) {
        int responseCode = -1;
        String responseBody = null;
        JSONObject responseJson = new JSONObject();
        JSONObject response = null;
        long startTime = 0;
        String xCorrelationID = null;
        URL urlObj = null;
        try {
            if(env.getProperty("is.proxy.required").equals("Y")) {
                String proxyip = env.getProperty("proxyip");
                String proxyport = env.getProperty("proxyport");
                String username = env.getProperty("username");
                String password = env.getProperty("password");
                System.setProperty("https.proxyUser", username);
                System.setProperty("https.proxyPassword",password);
                System.setProperty("https.proxyHost", proxyip);
                System.setProperty("https.proxyPort", proxyport);
                System.setProperty("http.proxyUser", username);
                System.setProperty("http.proxyPassword", password);
                System.setProperty("http.proxyHost", proxyip);
                System.setProperty("http.proxyPort", proxyport);
            }

            byte[] postData = (requestData);

            urlObj = new URL(url);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)urlObj.openConnection();

            //Temp for testing purpose
//            HttpURLConnection httpsURLConnection = (HttpURLConnection) urlObj.openConnection();

            //set timeputs to 10 seconds
            httpsURLConnection.setConnectTimeout(10000);
            httpsURLConnection.setReadTimeout(10000);
            String xpaytoken =  generateXPayToken(header);
            String requestId = (String) header.get("xRequestId");

            LOGGER.debug("url =:"+url);
            //LOGGER.debug("xpaytoken = "+xpaytoken);
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
            httpsURLConnection.setRequestProperty("x-request-id", requestId);
            httpsURLConnection.setRequestProperty("x-pay-token", xpaytoken);
            httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(header.get("requestBody").toString().getBytes("UTF-8").length));
            startTime = System.currentTimeMillis();
            OutputStream out = httpsURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpsURLConnection.getResponseCode();
            //success

            if (responseCode == HttpStatus.SC_OK) {
                responseBody = convertStreamToString(httpsURLConnection.getInputStream());
                response = new JSONObject(responseBody);
                responseJson.put("response",response);
                responseJson.put(HCEConstants.STATUS_CODE, HCEMessageCodes.getSUCCESS());
                responseJson.put(HCEConstants.STATUS_MESSAGE,"Success");
                Map<String, List<String>> responseheader = httpsURLConnection.getHeaderFields();
                xCorrelationID = responseheader.get("X-CORRELATION-ID").get(0);
                LOGGER.debug("Enroll device https response xCorrelationID = " + xCorrelationID);
                LOGGER.debug("Enroll device https response = " + responseBody);
            } else {
                //failure
                responseBody = convertStreamToString(httpsURLConnection.getErrorStream());
                LOGGER.debug("Resister Device response = "+responseBody);
                Map<String, List<String>> responseheader = httpsURLConnection.getHeaderFields();
                xCorrelationID = responseheader.get("X-CORRELATION-ID").get(0);
                LOGGER.debug("Enroll device https response xCorrelationID = " + xCorrelationID);

                response = new JSONObject(responseBody);
                responseJson.put("response",response);
                responseJson.put(HCEConstants.STATUS_CODE,responseCode);
                if(response.has("errorResponse")) {
                    responseJson.put("statusMessage", response.getJSONObject("errorResponse").get("message"));
                }
                else if(response.has("responseStatus")){
                    responseJson.put(HCEConstants.STATUS_MESSAGE, response.getJSONObject("responseStatus").get("message"));
                }
                else
                {
                    responseJson.put(HCEConstants.STATUS_MESSAGE,"Unknown");
                }
            }
        } catch (IOException ioe) {
            LOGGER.error("Exception Occured in SendRequest->postHttpRequest",ioe);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        } catch (Exception e) {
            LOGGER.error("Exception Occured in SendRequest->postHttpRequest",e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }finally {
            final long endTime = System.currentTimeMillis();
            final long totalTime = endTime - startTime;
            if (null !=response) {
                HCEUtil.writeTdrLog(totalTime, Integer.toString(responseCode), xCorrelationID, request, response.toString(),urlObj.getPath());

            }
        }
        return responseJson;
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
        String sharedSecret = env.getProperty("sharedSecret");
        byte[] bsharedSecret = sharedSecret.getBytes();
        // JSONObject object=new JSONObject(prepareHeaderRequest.get("requestBody"));
        long utcTimestamp = System.currentTimeMillis() / 1000L;
        String xPayToken = "xv2:" + utcTimestamp + ":";
        //String query_string = "apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc";
        //String resource_path="vts/clients/vClientID/devices/clientDeviceID";
        //String hashInput = (utcTimestamp+resource_path+query_string+object.toString());
        String hashInput = (utcTimestamp+(String)prepareHeaderRequest.get("resourcePath")+prepareHeaderRequest.get("queryString")+prepareHeaderRequest.get("requestBody"));
        //System.out.println("hashInput:"+hashInput);
        try {
            byte[] bHmacSha256 = MessageDigestUtil.hMacSha256(hashInput.getBytes("UTF-8"),bsharedSecret);
            hmacSha256 = ArrayUtil.getHexString(bHmacSha256).toLowerCase();
        } catch (Exception e) {
            LOGGER.error("Exception Occured" + e);
        }
        /*System.out.println("hmacSha256:    "+hmacSha256);
        System.out.println("X-PAY-TOKEN IS :   "+xPayToken + hmacSha256);*/
        return xPayToken + hmacSha256;
    }

}
