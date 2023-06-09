package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.mdes.DeviceRegistrationMdes;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceVts;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;

/**
 * Created by rishikesh.kumar on 02-07-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DeviceDetailServiceImplTest {

    @Autowired
    DeviceDetailRepository deviceDetailRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);
    private String paymentAppInstanceId = "";
    private String activationCode="";

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");
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
    public void registerDevice() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        request.put("userId",userID);
        request.put("clientDeviceID",clientDeviceID);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "200");
    }

    @Test
    public void registerDeviceWhenMCeligibilityFails() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("userId",userID);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("clientDeviceID",clientDeviceID);
        request.put("mdes",mdes);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "200");
    }

    @Test
    public void registerDeviceWithInvalidUser() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.remove("userId");
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "205");

    }

    @Test
    public void registerDeviceWithInvalidDeviceId() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        request.put("userId",userID);
        request.remove("clientDeviceID");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "703");
    }

    @Test
    public void registerDeviceWithoutVtsRequest() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        request.put("userId",userID);
        request.put("clientDeviceID",clientDeviceID);
        request.remove("vts");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "500");
    }

    @Test
    public void registerDeviceWithInvalidRequest() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        request.put("random","");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "706");
    }

    @Test
    public void unRegister() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        Map unregisterReq = DefaultTemplateUtils.buildRequest("/unregisterRequest.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("userId",userID);
        unregisterReq.put("userId",userID);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        unregisterReq.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("clientDeviceID",clientDeviceID);
        unregisterReq.put("imei",deviceInfo.get("imei"));
        request.put("mdes",mdes);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/deRegister",unregisterReq);
        assertResponse(unregisterReaponse, "704");
    }

    @Test
    public void unRegisterWithoutRegistering() throws Exception {
        Map unregisterReq = DefaultTemplateUtils.buildRequest("/unregisterRequest.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        unregisterReq.put("userId",userID);
        unregisterReq.put("paymentAppInstanceId",paymentAppInstanceId);
        unregisterReq.put("imei",DefaultTemplateUtils.randomString(20));
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/deRegister",unregisterReq);
        assertResponse(unregisterReaponse, "704");
    }

    @Test
    public void unRegisterWihoutUserId() throws Exception {
        Map unregisterReq = DefaultTemplateUtils.buildRequest("/unregisterRequest.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        unregisterReq.remove("userId");
        unregisterReq.put("paymentAppInstanceId",paymentAppInstanceId);
        unregisterReq.put("imei",DefaultTemplateUtils.randomString(20));
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/deRegister",unregisterReq);
        assertResponse(unregisterReaponse, "500");
    }

    /*@Test
    public void deleteVISACards() throws Exception {
        List<CardDetails> cardDetails = deviceDetailRepository.findAll().get(0).getCardDetails();
        deviceDetailService.deleteVISACards(cardDetails);
    }*/

    @Test
    public void unRegisterWithInvalidReq() throws Exception {
        Map unregisterReq = DefaultTemplateUtils.buildRequest("/unregisterRequest.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        unregisterReq.put("Random","");
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/deRegister",unregisterReq);
        assertResponse(unregisterReaponse, "706");
    }


}
