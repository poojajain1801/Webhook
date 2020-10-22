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
 * Created by Rishikesh.kumar on 1/05/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DeviceRegistrationControllerTest {
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
    public void registerDeviceWithNullRequest() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",null);
        assertResponse(regDeviceReaponse, "500");
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

    @Test
    public void unRegisterWithInvalidReq() throws Exception {
        Map unregisterReq = DefaultTemplateUtils.buildRequest("/unregisterRequest.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        unregisterReq.put("Random","");
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/deRegister",unregisterReq);
        assertResponse(unregisterReaponse, "706");
    }

    @Test
    public void enrollDeviceForDAS() throws Exception {
        registerUser();
        registerDevice();
        Map enrollDeviceForDASRequest = DefaultTemplateUtils.buildRequest("/enrollDasReq.json");
        enrollDeviceForDASRequest.put("clientDeviceID",clientDeviceID);
        Map enrollDeviceForDASResp = ServiceUtils.servicePOSTResponse("device/enrollDeviceDas",enrollDeviceForDASRequest);
        assertResponse(enrollDeviceForDASResp, "200");
    }

    @Test
    public void enrollDeviceForDASWithNullRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/enrollDasReq.json");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/enrollDeviceDas",null);
        assertResponse(regDeviceReaponse, "500");
    }

    @Test
    public void enrollDeviceForDASWihoutClientDeviceId() throws Exception {
        registerUser();
        registerDevice();
        Map enrollDeviceForDASRequest = DefaultTemplateUtils.buildRequest("/enrollDasReq.json");
        enrollDeviceForDASRequest.remove("clientDeviceID");
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/enrollDeviceDas",enrollDeviceForDASRequest);
        assertResponse(unregisterReaponse, "704");
    }

    @Test
    public void enrollDeviceForDASInvalidClientDeviceId() throws Exception {
        registerUser();
        registerDevice();
        Map enrollDeviceForDASRequest = DefaultTemplateUtils.buildRequest("/enrollDasReq.json");
        enrollDeviceForDASRequest.put("clientDeviceID", clientDeviceID + "222");
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/enrollDeviceDas",enrollDeviceForDASRequest);
        assertResponse(unregisterReaponse, "704");
    }

    @Test
    public void enrollDeviceForDASwithoutDeviceInfo() throws Exception {
        Map enrollDeviceForDASRequest = DefaultTemplateUtils.buildRequest("/enrollDasReq.json");
        enrollDeviceForDASRequest.remove("deviceInfo");
        Map unregisterReaponse = ServiceUtils.servicePOSTResponse("device/enrollDeviceDas",enrollDeviceForDASRequest);
        assertResponse(unregisterReaponse, "500");
    }

}