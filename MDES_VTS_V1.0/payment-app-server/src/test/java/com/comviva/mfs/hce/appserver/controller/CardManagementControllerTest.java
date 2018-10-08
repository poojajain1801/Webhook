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
public class CardManagementControllerTest {
    @Resource
    private WebApplicationContext webApplicationContext;
    private String userID = "";
    private String paymentAppInstanceId = "";


    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");

    }//
    @Test
    public void registerUser() throws Exception {

        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        userID = DefaultTemplateUtils.randomString(8);
        request.put("userId",userID);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",request);
        assertResponse(registerUserResp, "200");
    }
    @Test
    public void activateUser() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/ActivateUserReq.json");
        request.put("userId",userID);
        Map activateUserResp = ServiceUtils.servicePOSTResponse("user/activateUser",request);
        assertResponse(activateUserResp, "200");

    }
    @Test
    public void registerDevice() throws Exception {
        activateUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("userId",userID);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "200");

    }
    @Test
    public void addCard() throws Exception {
        registerDevice();
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        Map addCardResponse = ServiceUtils.servicePOSTResponse("card/checkCardEligibility",request);
        assertResponse(addCardResponse, "200");

    }

    @Test
    public void continueDigitization() throws Exception {
        addCard();
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        Map continueDegitizationResp = ServiceUtils.servicePOSTResponse("card/continueDigitization",request);
        assertResponse(continueDegitizationResp, "200");
    }

    @Test
    public void getAsset() throws Exception {

    }

    @Test
    public void activate() throws Exception {

    }

    @Test
    public void enrollPan() throws Exception {

    }

    @Test
    public void getCardMetadata() throws Exception {

    }

    @Test
    public void getContent() throws Exception {

    }

    @Test
    public void getPANData() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void notifyTransactionDetails() throws Exception {

    }

    @Test
    public void getRegistrationCode() throws Exception {

    }

    @Test
    public void registerWithTDS() throws Exception {

    }

    @Test
    public void registerWithTDS1() throws Exception {

    }

}