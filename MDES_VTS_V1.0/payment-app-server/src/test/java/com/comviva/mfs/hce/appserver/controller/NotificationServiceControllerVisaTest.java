package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;
import static org.junit.Assert.assertEquals;

/**
 * Created by rishikesh.kumar on 30-11-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NotificationServiceControllerVisaTest {

    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("");
    }

    @Test
    public void notifyLCMEvent() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyProvisionedToken.json");
        Map response = ServiceUtils.servicePOSTResponse("provisionedToken?eventType=TOKEN_STATUS_UPDATED&apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc",request);
        assertResponse(response, "707");
    }

    /*@Test
    public void notifyLCMEvent() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyProvisionedToken.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "provisionedToken?eventType=TOKEN_STATUS_UPDATED&apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc";
        String apiKey = "R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc";
        String eventType= "TOKEN_STATUS_UPDATED" ;
        String inputInJson = this.mapToJson(request);

        Mockito.when(notificationServiceVisaService.notifyLCMEvent(Mockito.any(NotificationServiceReq.class),apiKey,eventType)).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200" , responseCode);
    }*/

    @Test
    public void notifyLCMEventWithotDate() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyProvisionedToken.json");
        request.remove("date");
        Map response = ServiceUtils.servicePOSTResponse("provisionedToken?eventType=TOKEN_STATUS_UPDATED&apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc",request);
        assertResponse(response, "707");
    }

    @Test
    public void notifyCardMetadataUpdateEvent() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notificationServiceReq.json");
        Map response = ServiceUtils.servicePOSTResponse("panMetadata?apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc",request);
        assertResponse(response, "500");
    }

    @Test
    public void notifyTxnDetailsUpdateEvent() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notificationServiceReq.json");
        Map response = ServiceUtils.servicePOSTResponse("paymentTxns?eventType=TOKEN_STATUS_UPDATED&apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc",request);
        assertResponse(response, "708");
    }

    @Test
    public void notifyTxnDetailsUpdateEventWithoutProvisionedToken() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notificationServiceReq.json");
        request.remove("vProvisionedTokenID");
        Map response = ServiceUtils.servicePOSTResponse("paymentTxns?eventType=TOKEN_STATUS_UPDATED&apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc",request);
        assertResponse(response, "500");
    }
}
