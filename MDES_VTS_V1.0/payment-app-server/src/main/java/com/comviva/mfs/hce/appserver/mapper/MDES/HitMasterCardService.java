package com.comviva.mfs.hce.appserver.mapper.MDES;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.SSLContext;

import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.mdes.RequestResponseLoggingInterceptor;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
//import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class HitMasterCardService implements RestTemplateCustomizer
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
    private SSLContext sslContext = null;
    public ResponseEntity restfulServiceConsumerMasterCard(String url, String requestBody, String type,String id) {
        LOGGER.debug("Enter HitMasterCardService -> restfulServiceConsumerMasterCard");
        ResponseEntity<String> response = null;
        HttpEntity<String> entity = null;
        long startTime = 0;

        String proxyip = null;
        int proxyport = 0;
        Proxy proxy = null;

        this.headers = new HttpHeaders();
        this.headers.add("Accept", "application/json");
        this.headers.add("Accept", "application/pkix-cert");
        this.headers.add("Content-Type", "application/json");
//        this.headers.add("Content-Type", "application/pkix-cert");
        this.headers.add("Accept-Encoding", "deflate");
        this.headers.add("Connection", "Keep-Alive");
        this.headers.add("User-Agent", "Apache-HttpClient/4.1.1");


        //        this.headers.add("Host","services.mastercard.com");
        try {
            URL currUrl = new URL(url);
            LOGGER.info(currUrl.getHost());
            this.headers.add("Host",currUrl.getHost());
            if ((type.equalsIgnoreCase("GET")) || (requestBody.equalsIgnoreCase(null)) || (requestBody.isEmpty())) {
                entity = new HttpEntity(this.headers);
            } else {
                entity = new HttpEntity(requestBody, this.headers);
            }
            LOGGER.debug("Configuring SSL...");
            // RestTemplate restTemplate = restTemplate();
            Map idMap = new HashMap();
            /*if (!(id.equalsIgnoreCase("")|| id.isEmpty()))
            {
                +"/{assetId}";
                //idMap.put("id","checkEligibility");

                idMap.put("assetId","95d4cd38-36fc-4b26-8795-06a3b00acf3b");

            }*/
            url =url+"/{id}";
            idMap.put("id",id);

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            if(env.getProperty("is.proxy.required").equals("Y"))
            {
                proxyip = env.getProperty("proxyip");
                proxyport = Integer.parseInt(env.getProperty("proxyport"));
                proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyip,proxyport));
                requestFactory.setProxy(proxy);
            }
            RestTemplate restTemplate = restTemplate();
            restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));

            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
            stringHttpMessageConverter.setWriteAcceptCharset(false);
            restTemplate.getMessageConverters().add(0, stringHttpMessageConverter);

            LOGGER.debug("Request = " + (String)entity.getBody());
            LOGGER.debug("URL---- = " + url);
            LOGGER.info("info---- = " + url);
            startTime = System.currentTimeMillis();
            if ("POST".equals(type)) {
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

        String keystorepa = env.getProperty("truststorepass");
        String trustorename = "classpath:"+env.getProperty("truststoreName");
        sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(ResourceUtils.getFile(trustorename), keystorepa.toCharArray(), keystorepa.toCharArray())
                .loadTrustMaterial(ResourceUtils.getFile(trustorename), keystorepa.toCharArray())
                .build();
        RestTemplate restTemplate= new RestTemplate();
        customize(restTemplate);
        return restTemplate;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        String username = env.getProperty("username");
        String password = env.getProperty("password");
        String proxyip = env.getProperty("proxyip");
        String proxyport = env.getProperty("proxyport");

        HttpClient client = null;
        if(!env.getProperty("is.proxy.required").equals("Y")) {
            client = HttpClients.custom().setSSLContext(sslContext).build();
        }else {
            HttpHost proxy = new HttpHost(proxyip,Integer.valueOf(proxyport));
            client = HttpClientBuilder.create()
                    .setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {

                        @Override
                        public HttpHost determineProxy(HttpHost target,
                                                       HttpRequest request, HttpContext context)
                                throws HttpException {

                            return super.determineProxy(target, request, context);
                        }

                    }).setSSLContext(sslContext)
                    .build();
        }

        restTemplate.setRequestFactory(
                new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(client)));
    }
}