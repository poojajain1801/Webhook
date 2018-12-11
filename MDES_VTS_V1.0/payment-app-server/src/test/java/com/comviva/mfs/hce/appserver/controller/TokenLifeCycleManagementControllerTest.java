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
 * Created by rishikesh.kumar on 05-12-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TokenLifeCycleManagementControllerTest {

    @Resource
    private WebApplicationContext webApplicationContext;
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");
    }

    @Test
    public void getTokenStatus() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenStatus.json");
        Map response = ServiceUtils.servicePOSTResponse("token/getTokenStatus",request);
        assertResponse(response, "500");
    }

    @Test
    public void lifeCycleManagementVisa() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/lifeCycleManagementVisaRequest.json");
        Map response = ServiceUtils.servicePOSTResponse("token/lifeCycleManagementVisa",request);
        assertResponse(response, "500");
    }

    @Test
    public void registerUser() throws Exception {
        Map UserRegistrationRequest = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        UserRegistrationRequest.put("userId",userID);
        UserRegistrationRequest.put("clientDeviceID",clientDeviceID);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",UserRegistrationRequest);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void getTokenList() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/getTokenListRequest.json");
        request.put("userId",userID);
        Map response = ServiceUtils.servicePOSTResponse("token/getTokenList",request);
        assertResponse(response, "707");
    }

    @Test
    public void getTokenListWithInvalidUser() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenListRequest.json");
        Map response = ServiceUtils.servicePOSTResponse("token/getTokenList",request);
        assertResponse(response, "205");
    }

}
