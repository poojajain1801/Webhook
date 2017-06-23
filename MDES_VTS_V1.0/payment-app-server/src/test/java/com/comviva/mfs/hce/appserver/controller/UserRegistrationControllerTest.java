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
    String userID = "";
    String activationCode="";

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
        assertResponse(registerUseResponse,"300");
    }
    @Test
    public void activateUserSuccess() throws Exception {
        registerUserSuccess();
        Map request = DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
        request.put("userId",userID);
        request.put("activationCode",activationCode);
        Map activateUserResponse = ServiceUtils.servicePOSTResponse("activateUser",request);
        assertResponse(activateUserResponse, "200");

    }
    @Test
    public void activateUserFailedForMissingField() throws Exception{
        registerUserSuccess();
         Map request=DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
         userID="";
         request.put("userId",userID);
         request.put("activationCode",activationCode);
        Map activateUserResponse = ServiceUtils.servicePOSTResponse("activateUser",request);
        assertResponse(activateUserResponse, "300");
    }
    @Test
    public void activateUserFailedForWrongUserId() throws Exception{
        registerUserSuccess();
        Map request=DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
        userID="wrong_userID";
        request.put("userId",userID);
        request.put("activationCode",activationCode);
        Map activateUserResponse = ServiceUtils.servicePOSTResponse("activateUser",request);
        assertResponse(activateUserResponse,"203");
    }
    @Test
    public void activateUserFailedForWrongActivationCode() throws Exception{
        registerUserSuccess();
        Map request =DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
        request.put("userId",userID);
        request.put("activationCode","wrong_activationCode");
        Map activateUserResponse = ServiceUtils.servicePOSTResponse("activateUser",request);
        assertResponse(activateUserResponse,"202");
    }
    @Test
    public void activateUserFailedForUserAlreadyActivated() throws Exception{
        registerUserSuccess();
        activateUserSuccess();
        Map request = DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
        request.put("userId",userID);
        request.put("activationCode",activationCode);
        Map activateUserResponse = ServiceUtils.servicePOSTResponse("activateUser",request);
        assertResponse(activateUserResponse, "204");
    }
}