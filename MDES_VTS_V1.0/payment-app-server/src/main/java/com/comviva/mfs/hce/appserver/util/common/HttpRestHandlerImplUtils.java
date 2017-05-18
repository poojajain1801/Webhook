package com.comviva.mfs.hce.appserver.util.common;

        import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Tanmay.Patel on 1/17/2017.
 */
public class HttpRestHandlerImplUtils implements HttpRestHandlerUtils {


    /**
     * @param url
     * @param parametersMap
     * @return
     */
    public String restfulServieceConsumer(String url, MultiValueMap parametersMap)
    {
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        final HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<MultiValueMap<String,Object>>(parametersMap ,headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, entity, String.class);
    }

}
