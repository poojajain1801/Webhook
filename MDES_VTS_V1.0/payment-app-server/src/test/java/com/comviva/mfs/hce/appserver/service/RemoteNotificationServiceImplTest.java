package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
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
import java.util.HashMap;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;

/**
 * Created by rishikesh.kumar on 04-12-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RemoteNotificationServiceImplTest {
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/mpamanagement/1/0");
    }

    @Test
    public void sendRemoteNotificationMessage() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/remoteNotificationMessageReq.json");
        Map response = ServiceUtils.servicePOSTResponse("/sendRemoteNotificationMessage",request);
        assertResponse(response, "708");
    }

    @Test
    public void sendGenericRns() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/rnsGenericRequest.json");
        request.put("idType", UniqueIdType.MDES);
        request.put("registrationId","yui098789ihnm");
        HashMap notifyTokenUpdatedMap = (HashMap) JsonUtil.jsonStringToHashMap("");
        request.put("rnsData",notifyTokenUpdatedMap);
        Map response = ServiceUtils.servicePOSTResponse("/sendGenericRemoteNotificationMessage",request);
        assertResponse(response, "708");
    }

    @Test
    public void sendRemoteNotificationMessageWithoutReqId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/remoteNotificationMessageReq.json");
        request.remove("requestId");
        Map response = ServiceUtils.servicePOSTResponse("/sendRemoteNotificationMessage",request);
        assertResponse(response, "708");
    }
}
