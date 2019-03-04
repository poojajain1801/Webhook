package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ProvisionTokenGivenPanEnrollmentIdRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import com.comviva.mfs.hce.appserver.util.mdes.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import java.util.HashMap;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;
import static org.junit.Assert.assertEquals;

/**
 * Created by rishikesh.kumar on 03-12-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RemoteNotificationControllerTest {
    @MockBean
    private RemoteNotificationService remoteNotificationService;
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
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/mpamanagement/1/0/sendRemoteNotificationMessage";
        String inputInJson = this.mapToJson(request);

        Mockito.when(remoteNotificationService.sendRemoteNotificationMessage(Mockito.any(RemoteNotificationRequest.class))).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200" , responseCode);
    }

    @Test
    public void sendRemoteNotificationMessageWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/remoteNotificationMessageReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/mpamanagement/1/0/sendRemoteNotificationMessage";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");
        Mockito.when(remoteNotificationService.sendRemoteNotificationMessage(Mockito.any(RemoteNotificationRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("707" , responseCode);
    }

    @Test
    public void sendRemoteNotificationMessageWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/remoteNotificationMessageReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/mpamanagement/1/0/sendRemoteNotificationMessage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(remoteNotificationService.sendRemoteNotificationMessage(Mockito.any(RemoteNotificationRequest.class))).thenThrow(Exception.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("500" , responseCode);
    }

    @Test
    @Ignore
    public void sendGenericRns() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/rnsGenericRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/mpamanagement/1/0/sendGenericRemoteNotificationMessage";
        String inputInJson = this.mapToJson(request);

        Mockito.when(remoteNotificationService.sendRemoteNotification(Mockito.any(RnsGenericRequest.class))).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200" , responseCode);
    }


    private String mapToJson(Object object) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
