package com.comviva.mfs.hce.appserver.service;

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
 * Created by rishikesh.kumar on 28-11-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NotificationServiceTest {
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/digitization/1/0/");
    }

    @Test
    public void notifyTokenUpdated() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTokenUpdatedReq.json");
        Map notifyTokenUpdatedResp = ServiceUtils.servicePOSTResponse("notifyTokenUpdated",request);
        assertResponse(notifyTokenUpdatedResp, "500");
    }

    @Test
    public void notifyTransactionDetails() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTransactionsDetailsReq.json");
        Map notifyTransactionsDetailsResp = ServiceUtils.servicePOSTResponse("notifyTransactionDetails",request);
        assertResponse(notifyTransactionsDetailsResp, "500");
    }

    @Test
    public void pushTransctionDetails() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/pushTransactionDetailsReq.json");
        Map pushTransactionDetailsReap = ServiceUtils.servicePOSTResponse("pushTransactionDetails",request);
        assertResponse(pushTransactionDetailsReap, "500");
    }
}
