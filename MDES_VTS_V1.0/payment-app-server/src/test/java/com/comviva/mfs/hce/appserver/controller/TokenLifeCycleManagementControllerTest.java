package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenListRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenStatusRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTokenUpdatedReq;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
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
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;
import static org.junit.Assert.assertEquals;

/**
 * Created by rishikesh.kumar on 05-12-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TokenLifeCycleManagementControllerTest {
    @MockBean
    private TokenLifeCycleManagementService tokenLifeCycleManagementService;
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
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/token/getTokenStatus";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.getTokenStatus(Mockito.any(GetTokenStatusRequest.class))).thenReturn(successScenario);
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
    public void getTokenStatusWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenStatus.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/token/getTokenStatus";
        HCEActionException hceActionException = new HCEActionException("205");
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.getTokenStatus(Mockito.any(GetTokenStatusRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("205" , responseCode);
    }

    @Test
    public void getTokenStatusWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenStatus.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/token/getTokenStatus";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.getTokenStatus(Mockito.any(GetTokenStatusRequest.class))).thenThrow(Exception.class);
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
    public void lifeCycleManagementVisa() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/lifeCycleManagementVisaRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/token/lifeCycleManagementVisa";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.lifeCycleManagementVisa(Mockito.any(LifeCycleManagementVisaRequest.class))).thenReturn(successScenario);
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
    public void lifeCycleManagementVisaWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/lifeCycleManagementVisaRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("205");
        String URI = "/api/token/lifeCycleManagementVisa";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.lifeCycleManagementVisa(Mockito.any(LifeCycleManagementVisaRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("205" , responseCode);
    }

    @Test
    public void lifeCycleManagementVisaWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/lifeCycleManagementVisaRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/token/lifeCycleManagementVisa";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.lifeCycleManagementVisa(Mockito.any(LifeCycleManagementVisaRequest.class))).thenThrow(Exception.class);
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
    public void getTokenList() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenListRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/token/getTokenList";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.getTokenList(Mockito.any(GetTokenListRequest.class))).thenReturn(successScenario);
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
    public void getTokenListWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenListRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/token/getTokenList";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("205");
        Mockito.when(tokenLifeCycleManagementService.getTokenList(Mockito.any(GetTokenListRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("205" , responseCode);
    }

    @Test
    public void getTokenListWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenListRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/token/getTokenList";
        String inputInJson = this.mapToJson(request);
        Mockito.when(tokenLifeCycleManagementService.getTokenList(Mockito.any(GetTokenListRequest.class))).thenThrow(Exception.class);
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

    private String mapToJson(Object object) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
