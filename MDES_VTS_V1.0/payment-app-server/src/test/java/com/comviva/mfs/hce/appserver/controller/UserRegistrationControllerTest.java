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


    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/user/");

    }
    @Test
    public void registerUser() throws Exception {

        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        userID = DefaultTemplateUtils.randomString(8);
        request.put("userId",userID);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("userRegistration",request);
        assertResponse(regDeviceReaponse, "200");
    }

    @Test
    public void activateUser() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
        request.put("userId",userID);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("activateUser",request);
        assertResponse(regDeviceReaponse, "200");

    }

}