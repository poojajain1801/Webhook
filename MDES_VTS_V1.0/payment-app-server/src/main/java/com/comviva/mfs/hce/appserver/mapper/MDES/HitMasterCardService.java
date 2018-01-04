package com.comviva.mfs.hce.appserver.mapper.MDES;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class HitMasterCardService {

    @Autowired
    protected Environment env;
    private HttpHeaders headers;
    public HitMasterCardService(Environment env) {
        this.env = env;
    }
    public HitMasterCardService()
    {

    }
    private static final Logger LOGGER = LoggerFactory.getLogger(HitMasterCardService.class);

    public ResponseEntity restfulServiceConsumerMasterCard(String url, String requestBody, String type) {
        LOGGER.debug("Enter HitMasterCardService -> restfulServiceConsumerMasterCard");
        String result="";
        JSONObject jsonObject = null;
        JSONObject jsonResponse=null;
        ResponseEntity<String> response=null;
        String strResponse =null;
        HttpEntity<String> entity = null;
        SimpleClientHttpRequestFactory requestFactory = null;

        headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        headers.add("Accept-Encoding","deflate");
        headers.add("Connection","Keep-Alive");
        headers.add("User-Agent", "Apache-HttpClient/4.1.1");
        try {
            entity = new HttpEntity<>(requestBody, headers);
            requestFactory = new SimpleClientHttpRequestFactory();
            if (env.getProperty("is.proxy.required").equals("Y")) {
            String proxyip = env.getProperty("proxyip");
            int proxyport = Integer.parseInt(env.getProperty("proxyport"));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyip, proxyport));
            requestFactory.setProxy(proxy);

             }
             RestTemplate restTemplate = new RestTemplate(requestFactory);
            System.out.println("Request = "+entity.getBody());

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
            LOGGER.error("Exception occurred in HitMasterCardService");
            LOGGER.debug("Exception occurred in HitMasterCardService -> restfulServiceConsumerMasterCard");
            LOGGER.debug("Exit HitMasterCardService -> restfulServiceConsumerMasterCard");

        }
        return response;
    }
}
