package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

@Setter
public class HitVisaServices extends VtsRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HitVisaServices.class);

    public HitVisaServices(Environment env) {
        super(env);
    }

    public ResponseEntity restfulServiceConsumerVisa(String url, String requestBody,String resourcePath,String type) {

        LOGGER.debug("Inside HitVisaServices->restfulServiceConsumerVisa");
        JSONObject prepareHeaderRequest=new JSONObject();
        String xRequestId = String.format("%014X", Calendar.getInstance().getTime().getTime());
        xRequestId = xRequestId + ArrayUtil.getHexString(ArrayUtil.getRandom(10));
        prepareHeaderRequest.put("xRequestId",xRequestId);
        URL objUrl = null;
        try {
             objUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            LOGGER.debug("Exception Occurred HitVisaServices->restfulServiceConsumerVisa");
        }
        String quryString = objUrl.getQuery();
        prepareHeaderRequest.put("queryString",quryString);
        prepareHeaderRequest.put("resourcePath",resourcePath);
        if(!(requestBody.equalsIgnoreCase("null"))||(requestBody.isEmpty()))
             prepareHeaderRequest.put("requestBody",requestBody);

        HttpEntity<String> entity = null;
        prepareHeader(prepareHeaderRequest);
        if (type.equalsIgnoreCase("GET")||(requestBody.equalsIgnoreCase(null))||(requestBody.isEmpty()))
            entity = new HttpEntity<>(headers);
        else
            entity = new HttpEntity<>(requestBody, headers);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if(env.getProperty("is.proxy.required").equals("Y"))
        {
            String proxyip = env.getProperty("proxyip");
            int proxyport = Integer.parseInt(env.getProperty("proxyport"));
            Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyip,proxyport));
            requestFactory.setProxy(proxy);

        }
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String result="";
        JSONObject jsonObject = null;
        JSONObject jsonResponse=null;
        ResponseEntity<String> response=null;
        String strResponse =null;
        System.out.println("Request = "+entity.getBody());
        try {
            if("POST".equals(type)) {
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            }else if("PUT".equals(type)){
                response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            }
            else if("GET".equalsIgnoreCase(type))
            {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            }
        }catch (Exception e){
            String error = ((HttpClientErrorException) e).getResponseBodyAsString();
            String xCorrelationId = ((HttpClientErrorException)e).getResponseHeaders().get("X-CORRELATION-ID").toString();
            HttpHeaders responseHeaders = ((HttpClientErrorException)e).getResponseHeaders();
            HttpStatus statusCode = ((HttpClientErrorException)e).getStatusCode();
            ResponseEntity<String> errorResponse = new ResponseEntity(error, responseHeaders ,statusCode);
            LOGGER.debug(" HitVisaServices->xCorrelationId : "+xCorrelationId);
            LOGGER.debug(" HitVisaServices->error: "+error);
            LOGGER.debug("Exception occurred in HitVisaServices->restfulServiceConsumerVisa");
            LOGGER.debug("Exit HitVisaServices->restfulServiceConsumerVisa");
            return errorResponse;

        }
        LOGGER.debug("Exit HitVisaServices->restfulServiceConsumerVisa");

        return response;
    }


}