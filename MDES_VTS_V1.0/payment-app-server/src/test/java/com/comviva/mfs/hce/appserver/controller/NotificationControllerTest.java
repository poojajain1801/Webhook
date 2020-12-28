package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTokenUpdatedReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTransactionDetailsReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.PushTransctionDetailsReq;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
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


import static org.junit.Assert.assertEquals;

/**
 * Created by rishikesh.kumar on 28-11-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NotificationControllerTest {
    @MockBean
    private CardDetailService cardDetailService;
    @MockBean
    private TransactionManagementService transactionManagementService;
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/digitization/1/0/");
    }

    @Test
    public void notifyTokenUpdated() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTokenUpdatedReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/digitization/1/0/notifyTokenUpdated";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.notifyTokenUpdated(Mockito.any(NotifyTokenUpdatedReq.class))).thenReturn(successScenario);
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
    public void notifyTokenUpdatedWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTokenUpdatedReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/digitization/1/0/notifyTokenUpdated";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("703");
        Mockito.when(cardDetailService.notifyTokenUpdated(Mockito.any(NotifyTokenUpdatedReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("703" , responseCode);
    }

    @Test
    public void notifyTokenUpdatedWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTokenUpdatedReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/digitization/1/0/notifyTokenUpdated";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.notifyTokenUpdated(Mockito.any(NotifyTokenUpdatedReq.class))).thenThrow(Exception.class);
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
    public void notifyTransactionDetails() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTransactionsDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/digitization/1/0/notifyTransactionDetails";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.notifyTransactionDetails(Mockito.any(NotifyTransactionDetailsReq.class))).thenReturn(successScenario);
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
    public void notifyTransactionDetailsWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTransactionsDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("703");
        String inputInJson = this.mapToJson(request);
        String URI = "/digitization/1/0/notifyTransactionDetails";
        Mockito.when(cardDetailService.notifyTransactionDetails(Mockito.any(NotifyTransactionDetailsReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("703" , responseCode);
    }

    @Test
    public void notifyTransactionDetailsWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTransactionsDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String inputInJson = this.mapToJson(request);
        String URI = "/digitization/1/0/notifyTransactionDetails";
        Mockito.when(cardDetailService.notifyTransactionDetails(Mockito.any(NotifyTransactionDetailsReq.class))).thenThrow(Exception.class);
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
    public void pushTransctionDetails() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/pushTransactionDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/digitization/1/0/pushTransactionDetails";
        String inputInJson = this.mapToJson(request);

        Mockito.when(transactionManagementService.pushTransctionDetails(Mockito.any(PushTransctionDetailsReq.class))).thenReturn(successScenario);
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
    public void pushTransctionDetailsWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/pushTransactionDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/digitization/1/0/pushTransactionDetails";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("703");
        Mockito.when(transactionManagementService.pushTransctionDetails(Mockito.any(PushTransctionDetailsReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("703" , responseCode);
    }

    @Test
    public void pushTransctionDetailsWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/pushTransactionDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/digitization/1/0/pushTransactionDetails";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.pushTransctionDetails(Mockito.any(PushTransctionDetailsReq.class))).thenThrow(Exception.class);
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
    public void pushTransctionDetailsWithValidationException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/pushTransactionDetailsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/digitization/1/0/pushTransactionDetails";
        String inputInJson = this.mapToJson(request);
        HCEValidationException hceValidationException = new HCEValidationException("704","Device not Registered");
        Mockito.when(transactionManagementService.pushTransctionDetails(Mockito.any(PushTransctionDetailsReq.class))).thenThrow(hceValidationException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("704" , responseCode);
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
