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

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Map;


@Setter
public class HitVisaServices extends VtsRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HitVisaServices.class);

    public HitVisaServices(Environment env) {
        super(env);
    }

    /**
     * restfulServiceCOnsumerVisa
     * @param requestBody requestBody
     * @param resourcePath resourcePath
     * @param type type
     * @param url url
     * @return ResponseEntity
     * */
    public ResponseEntity restfulServiceConsumerVisa(String url, String requestBody,String resourcePath,String type) {
        String xRequestId = null;
        JSONObject prepareHeaderRequest= null;
        URL objUrl = null;
        long startTime = 0;
        String xCorrelationId = null;
        String quryString = null;
        HttpEntity<String> entity = null;
        RestTemplate restTemplate = null;
        String result="";
//        JSONObject jsonObject = null;
//        JSONObject jsonResponse=null;
        ResponseEntity<String> response=null;
//        String strResponse =null;
        String proxyip = null;
        int proxyport = 0;
        Proxy proxy = null;
        try{
            prepareHeaderRequest=new JSONObject();
            xRequestId = String.format("%014X", Calendar.getInstance().getTime().getTime());
            xRequestId = xRequestId + ArrayUtil.getHexString(ArrayUtil.getRandom(10));
            prepareHeaderRequest.put("xRequestId",xRequestId);
            objUrl = new URL(url);
            quryString = objUrl.getQuery();
            prepareHeaderRequest.put("queryString",quryString);
            prepareHeaderRequest.put("resourcePath",resourcePath);
            if(!("null").equalsIgnoreCase(requestBody)||(requestBody.isEmpty()))
                prepareHeaderRequest.put("requestBody",requestBody);
            prepareHeader(prepareHeaderRequest);
            if (type.equalsIgnoreCase("GET")||(requestBody.equalsIgnoreCase(null))||(requestBody.isEmpty()))
                entity = new HttpEntity<>(headers);
            else
                entity = new HttpEntity<>(requestBody, headers);

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            if(env.getProperty("is.proxy.required").equals("Y"))
            {
                proxyip = env.getProperty("proxyip");
                proxyport = Integer.parseInt(env.getProperty("proxyport"));
                proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyip,proxyport));
                requestFactory.setProxy(proxy);
            }
           // Gson msdf = new Gson(headers);
         //   Enumeration headerNames = headers.getHeaderNames();

            Map sdsd = entity.getHeaders();
            LOGGER.info("-------------------URL-------------------------"+url);
            LOGGER.debug("-------------------Begin Headers-------------------------");
            for (Object name : sdsd.entrySet()) {
                // search  for value
                Object value =  sdsd.get(name);
                //System.out.println("Key = " + name + ", Value = " + value);

                LOGGER.debug("Key = " + name + ", Value = " + value);

            }
            LOGGER.debug("-------------------End Headers-------------------------");

            restTemplate = new RestTemplate(requestFactory);

            //Restrict spring boot from adding extra header of its own.
            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
            stringHttpMessageConverter.setWriteAcceptCharset(false);
            restTemplate.getMessageConverters().add(0, stringHttpMessageConverter);

            startTime = System.currentTimeMillis();
            //url = "http://172.19.4.223:8080/test/Test";
            if("POST".equals(type)) {
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            }else if("PUT".equals(type)){
                response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            } else if("GET".equalsIgnoreCase(type)) {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            }
            if(response!=null){
                HttpHeaders headers = response.getHeaders();
                xCorrelationId = headers.get("X-CORRELATION-ID").get(0);
            }

        } catch (MalformedURLException e){
            LOGGER.debug("Exception Occurred HitVisaServices->restfulServiceConsumerVisa",e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        } catch(HttpClientErrorException httpClientException){
            LOGGER.error("Exeption occured",httpClientException);
            xCorrelationId = httpClientException.getResponseHeaders().get("X-CORRELATION-ID").toString();
            HttpHeaders responseHeaders = httpClientException.getResponseHeaders();
            HttpStatus statusCode = httpClientException.getStatusCode();
            String error = httpClientException.getResponseBodyAsString();
            if(error!=null && !error.isEmpty()){
                response = new ResponseEntity(error, responseHeaders ,statusCode);
            } else{
                response = new ResponseEntity(error, responseHeaders ,statusCode);
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

            return response;
        } catch(HCEActionException hitVisaServiceExp){
            LOGGER.debug("Exception Occurred HitVisaServices->restfulServiceConsumerVisa",hitVisaServiceExp);
            throw hitVisaServiceExp;
        } catch (Exception e){
            LOGGER.debug("Exception Occurred HitVisaServices->restfulServiceConsumerVisa",e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }finally {
            final long endTime = System.currentTimeMillis();
            final long totalTime = endTime - startTime;
            int statusCode = 0;
            if(response!=null){
                statusCode = response.getStatusCode().value();

            }
            if(null !=response) {
                HCEUtil.writeTdrLog(totalTime, Integer.toString(statusCode), xCorrelationId, requestBody, String.valueOf(response.getBody()),objUrl.getPath());
            }
        }
        return response;
    }

}
