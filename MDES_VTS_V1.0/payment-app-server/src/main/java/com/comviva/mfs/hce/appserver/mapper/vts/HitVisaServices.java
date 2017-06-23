package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Setter
public class HitVisaServices extends VtsRequest {

    public HitVisaServices(Environment env) {
        super(env);
    }

    public String restfulServiceConsumerVisa(String url, String requestBody, Map parametersMap) {
        JSONObject prepareHeaderRequest=new JSONObject();
        prepareHeaderRequest.put("xRequestId","generateXrequestId");
        prepareHeaderRequest.put("queryString","apiKey="+apiKey);
        prepareHeaderRequest.put("resourcePath","");
        prepareHeaderRequest.put("requestBody",requestBody);
        prepareHeader(prepareHeaderRequest);
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        final HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String,Object>>(parametersMap ,headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, entity, String.class);
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