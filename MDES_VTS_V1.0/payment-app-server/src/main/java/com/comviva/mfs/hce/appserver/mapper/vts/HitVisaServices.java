package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.google.gson.Gson;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Setter
public class HitVisaServices extends VtsRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HitVisaServices.class);

    public HitVisaServices(Environment env) {
        super(env);
    }

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
        JSONObject jsonObject = null;
        JSONObject jsonResponse=null;
        ResponseEntity<String> response=null;
        String strResponse =null;
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
            if(!(requestBody.equalsIgnoreCase("null"))||(requestBody.isEmpty()))
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
            LOGGER.debug("-------------------Begin Headers-------------------------");
            for (Object name : sdsd.keySet())
            {
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
            }
            else if("GET".equalsIgnoreCase(type))
            {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            }
            if(response!=null){
                HttpHeaders headers = response.getHeaders();
                xCorrelationId = headers.get("X-CORRELATION-ID").get(0);
            }

        }catch (MalformedURLException e){
            LOGGER.debug("Exception Occurred HitVisaServices->restfulServiceConsumerVisa",e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }catch(HttpClientErrorException httpClientException){
            LOGGER.error("Exeption occured",httpClientException);
            xCorrelationId = httpClientException.getResponseHeaders().get("X-CORRELATION-ID").toString();
            HttpHeaders responseHeaders = httpClientException.getResponseHeaders();
            HttpStatus statusCode = httpClientException.getStatusCode();
            String error = httpClientException.getResponseBodyAsString();
            if(error!=null && !error.isEmpty()){
                response = new ResponseEntity(error, responseHeaders ,statusCode);
            }else{
                response = new ResponseEntity(error, responseHeaders ,statusCode);
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

            return response;
        }catch(HCEActionException hitVisaServiceExp){
            LOGGER.debug("Exception Occurred HitVisaServices->restfulServiceConsumerVisa",hitVisaServiceExp);
            throw hitVisaServiceExp;
        }catch (Exception e){
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
                HCEUtil.writeTdrLog(totalTime, Integer.toString(statusCode), xCorrelationId, requestBody, String.valueOf(response.getBody()));
            }
        }
        return response;
    }
}