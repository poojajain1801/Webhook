package com.comviva.mfs.hce.appserver.mapper.MDES;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.SSLContext;

import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.mdes.RequestResponseLoggingInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class HitMasterCardService
{
    @Autowired
    protected Environment env;
    private HttpHeaders headers;

    @Autowired
    public HitMasterCardService(Environment env)
    {
        this.env = env;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HitMasterCardService.class);

    public ResponseEntity restfulServiceConsumerMasterCard(String url, String requestBody, String type,String id)
    {
        LOGGER.debug("Enter HitMasterCardService -> restfulServiceConsumerMasterCard");
        ResponseEntity<String> response = null;
        HttpEntity<String> entity = null;
        long startTime = 0;


        this.headers = new HttpHeaders();
        this.headers.add("Accept", "application/json");
        this.headers.add("Accept", "application/pkix-cert");
        this.headers.add("Content-Type", "application/json");
        this.headers.add("Content-Type", "application/pkix-cert");
        this.headers.add("Accept-Encoding", "deflate");
        this.headers.add("Connection", "Keep-Alive");
        this.headers.add("Host","mtf.services.mastercard.com");
        this.headers.add("User-Agent", "Apache-HttpClient/4.1.1");
        try
        {
            if ((type.equalsIgnoreCase("GET")) || (requestBody.equalsIgnoreCase(null)) || (requestBody.isEmpty())) {
                entity = new HttpEntity(this.headers);
            } else {
                entity = new HttpEntity(requestBody, this.headers);
            }
            LOGGER.debug("Configuring SSL...");
            RestTemplate restTemplate = restTemplate();
            Map idMap = new HashMap();
            /*if (!(id.equalsIgnoreCase("")|| id.isEmpty()))
            {
                +"/{assetId}";
                //idMap.put("id","checkEligibility");

                idMap.put("assetId","95d4cd38-36fc-4b26-8795-06a3b00acf3b");

            }*/
            url =url+"/{id}";
            idMap.put("id",id);




         /*   if(env.getProperty("is.proxy.required").equals("Y")) {
                Properties props = System.getProperties();
                props.put("http.proxyHost", "10.0.161.70");
                props.put("http.proxyPort", "80");
            }*/


            restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
            LOGGER.debug("Request = " + (String)entity.getBody());
            LOGGER.debug("URL---- = " + url);
            LOGGER.info("info---- = " + url);
            startTime = System.currentTimeMillis();
            if ("POST".equals(type))
            {
                LOGGER.debug("Request medthod  ########################################################## = " + type);
                LOGGER.info("Request medthod  ###########################################################= " + type);
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class,idMap);

                LOGGER.debug("Response STATUS******************************** = " + response.getStatusCode());
                LOGGER.debug("Response Body******************************** = " + (String)response.getBody());
               // LOGGER.info("Response ********************************" + (String)response.getBody());
            }
            else if ("PUT".equals(type))
            {
                response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class,idMap);
                LOGGER.debug("Response STATUS******************************** = " + response.getStatusCode());
                LOGGER.info("Response STATUS********************************" + response.getStatusCode());
                LOGGER.debug("Response ******************************** = " + (String)response.getBody());
               // LOGGER.info("Response ********************************" + (String)response.getBody());
            }
            else if ("GET".equalsIgnoreCase(type))
            {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class,idMap);
                LOGGER.debug("Response STATUS******************************** = " + response.getStatusCode());
                LOGGER.info("Response STATUS********************************" + response.getStatusCode());
                LOGGER.debug("Response ******************************** = " + (String)response.getBody());
               // LOGGER.info("Response ********************************" + (String)response.getBody());
            }
        }
        catch (HttpClientErrorException httpClintException)
        {
            LOGGER.error("Staus code recived from master card--->", httpClintException);
            LOGGER.error("Staus code recived from master card Messageeeee--->", httpClintException.getMessage());
            LOGGER.error("Staus code recived from master card   ResponseBodyAsString--->", httpClintException.getResponseBodyAsString());

            LOGGER.error("Staus code recived from master card--->", Integer.valueOf(httpClintException.getRawStatusCode()));
            LOGGER.error("Message recived from master card--->", httpClintException.getCause());
            LOGGER.error("Exeption occured", httpClintException);

            HttpHeaders responseHeaders = httpClintException.getResponseHeaders();
            HttpStatus statusCode = httpClintException.getStatusCode();
            String error = httpClintException.getResponseBodyAsString();
            if ((error != null) && (!error.isEmpty())) {
                response = new ResponseEntity(error, responseHeaders, statusCode);
            } else {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
            //return response;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred in HitMasterCardService", e);

            LOGGER.debug("Exit HitMasterCardService -> restfulServiceConsumerMasterCard");
        }
        finally {
            final long endTime = System.currentTimeMillis();
            final long totalTime = endTime - startTime;
            int statusCode = 0;
            if(response!=null){
                statusCode = response.getStatusCode().value();

            }
            if(null !=response) {
                String requestId = "";
                if (!(null==requestBody||(requestBody.isEmpty()))) {
                    JSONObject requestJson = new JSONObject(requestBody);

                    if (requestJson.has("requestId")) {
                        requestId = requestJson.getString("requestId");
                    }
                }
                HCEUtil.writeTdrLog(totalTime, Integer.toString(statusCode), requestId, requestBody, String.valueOf(response.getBody()),id);
            }
        }
        return response;
    }



    private RestTemplate restTemplate() throws Exception
    {
        String allNews = "changeit";
        String keystorepa = "changeit";
        String trustorename = "classpath:"+env.getProperty("truststoreName");
        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(ResourceUtils.getFile(trustorename), keystorepa.toCharArray(), keystorepa.toCharArray())
                .loadTrustMaterial(ResourceUtils.getFile(trustorename), allNews.toCharArray())
                .build();
        HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
        /*HttpHost proxy = new HttpHost("proxtserver", "");
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);*/
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
      /*  SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(ResourceUtils.getFile("classpath:truststore.jks"), secretkey.toCharArray(), secretkey.toCharArray()).loadTrustMaterial(ResourceUtils.getFile("classpath:keystore.jks"), secretkey.toCharArray()).build();
        HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();

       // HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(client));*/

        return new RestTemplate(factory);
    }
}
