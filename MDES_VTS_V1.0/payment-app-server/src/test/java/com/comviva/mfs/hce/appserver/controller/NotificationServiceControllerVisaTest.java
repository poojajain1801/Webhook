package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;

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
