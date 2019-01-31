package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

import org.json.JSONObject;
import org.junit.Before;
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

import java.util.HashMap;
import java.util.List;
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
    @MockBean
    private UserDetailService userDetailService;
    @Resource
    private WebApplicationContext webApplicationContext;

    private String activationCode = "";
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/user/");
    }

    @Test
    public void registerUserSuccess() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String, Object> successScenario = new HashMap<>();
        successScenario.put("Message", "Transaction Success");
        successScenario.put("reasonCode", "200");
        String URI = "/api/user/userRegistration";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.registerUser(Mockito.any(RegisterUserRequest.class))).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200", responseCode);
    }

    @Test
    public void registerUserWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("501");
        String URI = "/api/user/userRegistration";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.registerUser(Mockito.any(RegisterUserRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("501", responseCode);
    }

    @Test
    public void registerUserWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/user/userRegistration";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.registerUser(Mockito.any(RegisterUserRequest.class))).thenThrow(Exception.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("500", responseCode);
    }

    @Test
    public void getLanguage() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getLanguageReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String, Object> successScenario = new HashMap<>();
        successScenario.put("Message", "Transaction Success");
        successScenario.put("reasonCode", "200");
        String URI = "/api/user/getLanguage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.getLanguage(Mockito.any(GetLanguageReq.class))).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200", responseCode);
    }

    @Test
    public void getLanguageWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getLanguageReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("205");
        String URI = "/api/user/getLanguage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.getLanguage(Mockito.any(GetLanguageReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("205", responseCode);
    }

    @Test
    public void getLanguageWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getLanguageReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/user/getLanguage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.getLanguage(Mockito.any(GetLanguageReq.class))).thenThrow(Exception.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("500", responseCode);
    }

    @Test
    public void setLanguage() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/setLanguage.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String, Object> successScenario = new HashMap<>();
        successScenario.put("Message", "Transaction Success");
        successScenario.put("reasonCode", "200");
        String URI = "/api/user/setLanguage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.setLanguage(Mockito.any(SetLanguageReq.class))).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200", responseCode);
    }

    @Test
    public void setLanguageInvalidUser() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/setLanguage.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("205");
        String URI = "/api/user/setLanguage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.setLanguage(Mockito.any(SetLanguageReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("205", responseCode);
    }

    @Test
    public void setLanguageWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/setLanguage.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/user/setLanguage";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.setLanguage(Mockito.any(SetLanguageReq.class))).thenThrow(Exception.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("500", responseCode);
    }

    @Test
    public void userLifecycleManagement() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userLifeCycleManagementReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String, Object> successScenario = new HashMap<>();
        successScenario.put("Message", "Transaction Success");
        successScenario.put("reasonCode", "200");
        String URI = "/api/user/userLifecycleManagement";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.userLifecycleManagement(Mockito.any(UserLifecycleManagementReq.class))).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200", responseCode);
    }

    @Test
    public void userLifecycleManagementWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userLifeCycleManagementReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("205");
        String URI = "/api/user/userLifecycleManagement";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.userLifecycleManagement(Mockito.any(UserLifecycleManagementReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("205", responseCode);
    }

    @Test
    public void userLifecycleManagementWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userLifeCycleManagementReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/user/userLifecycleManagement";
        String inputInJson = this.mapToJson(request);
        Mockito.when(userDetailService.userLifecycleManagement(Mockito.any(UserLifecycleManagementReq.class))).thenThrow(Exception.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("500", responseCode);
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}

