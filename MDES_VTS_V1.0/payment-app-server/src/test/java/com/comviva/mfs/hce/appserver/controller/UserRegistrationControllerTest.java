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
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by Tanmay.Patel on 5/25/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserRegistrationControllerTest {
    @Resource
    private WebApplicationContext webApplicationContext;

    private String activationCode="";
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);


    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/user/");

    }
    @Test
    public void registerUserSuccess() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        userID = DefaultTemplateUtils.randomString(8);
        request.put("userId",userID);
        Map registerUserResponse = ServiceUtils.servicePOSTResponse("userRegistration",request);
        activationCode= (String) registerUserResponse.get("activationCode");
        assertResponse(registerUserResponse, "200");
    }

    @Test
    public void registerUserFailedForMissingField() throws Exception{
        Map request=DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        userID="";
        request.put("userId",userID);
        Map registerUseResponse=ServiceUtils.servicePOSTResponse("userRegistration",request);
        assertResponse(registerUseResponse,"500");
    }

    @Test
    public void registerUser() throws Exception {
        Map UserRegistrationRequest = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        UserRegistrationRequest.put("userId",userID);
        UserRegistrationRequest.put("clientDeviceID",clientDeviceID);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("/userRegistration",UserRegistrationRequest);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void registerUserWithoutClientDeviceId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        userID = DefaultTemplateUtils.randomString(8);
        request.put("userId",userID);
        request.put("clientDeviceID",null);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("/userRegistration",request);
        assertResponse(registerUserResp, "500");
    }

    @Test
    public void registerUserWithoutUserId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        request.put("random","");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("/userRegistration",request);
        assertResponse(registerUserResp, "706");
    }

    @Test
    public void registerUserWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        request.remove("userId");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("/userRegistration",request);
        assertResponse(registerUserResp, "500");
    }

    @Test
    public void registerUserWithNullRequest() throws Exception {
        Map request = null;
        Map registerUserResp = ServiceUtils.servicePOSTResponse("/userRegistration",request);
        assertResponse(registerUserResp, "500");
    }


}