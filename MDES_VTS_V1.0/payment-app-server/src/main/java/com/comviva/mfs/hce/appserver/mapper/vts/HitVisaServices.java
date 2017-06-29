package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Map;

@Setter
public class HitVisaServices extends VtsRequest {

    public HitVisaServices(Environment env) {
        super(env);
    }

    public String restfulServiceConsumerVisa(String url, String requestBody, Map parametersMap) {
        JSONObject prepareHeaderRequest=new JSONObject();
        String xRequestId = String.format("%014X", Calendar.getInstance().getTime().getTime());
        xRequestId = xRequestId + ArrayUtil.getHexString(ArrayUtil.getRandom(10));
        prepareHeaderRequest.put("xRequestId",xRequestId);
        prepareHeaderRequest.put("queryString","apiKey="+env.getProperty("apiKey"));
        prepareHeaderRequest.put("resourcePath","vts/panEnrollments");
        prepareHeaderRequest.put("requestBody",requestBody);
        prepareHeader(prepareHeaderRequest);
        final HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("172.19.7.180",8080));
        requestFactory.setProxy(proxy);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
       // final String sandBoxUrl = vtsUrl + PATH_SEPARATOR + prepareHeaderRequest.get("resourcePath")+ "?apiKey=" + apiKey;
        String result="";
        JSONObject jsonObject = null;
        JSONObject jsonResponse=null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        }catch (Exception e){
            e.printStackTrace();
            ((HttpClientErrorException)e).getResponseBodyAsString();
            ((HttpClientErrorException)e).getResponseHeaders();
        }
        return null;
    }

    public String restfulServiceConsumerVisaGet(String url, String requestBody) {
        JSONObject prepareHeaderRequest=new JSONObject();
        prepareHeaderRequest.put("xRequestId","generateXrequestId()");
        prepareHeaderRequest.put("queryString","apiKey="+apiKey);
        prepareHeaderRequest.put("resourcePath","vts/clients/");
        prepareHeaderRequest.put("requestBody",requestBody);
        prepareHeader(prepareHeaderRequest);
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        final HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String,Object>>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, entity, String.class);
    }

}