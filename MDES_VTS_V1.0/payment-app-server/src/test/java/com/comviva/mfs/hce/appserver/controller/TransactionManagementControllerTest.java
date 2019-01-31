package com.comviva.mfs.hce.appserver.controller;
import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.AddCardParm;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetRegistrationCodeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTransactionHistoryRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTransactionsRequest;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
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
import javax.xml.ws.Service;
import java.util.HashMap;
import java.util.Map;
import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;
import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
import static org.junit.Assert.assertEquals;

/**
 * Created by Rishikesh.kumar on 28-11-2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TransactionManagementControllerTest {
    @MockBean
    private TransactionManagementService transactionManagementService;
    @Resource
    private WebApplicationContext webApplicationContext;
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/transaction");
    }

    @Test
    public void getTransactionsMasterCard() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/transaction/getTransactions";
        String inputInJson = this.mapToJson(request);

        Mockito.when(transactionManagementService.getTransactionsMasterCard(Mockito.any(GetTransactionsRequest.class))).thenReturn(successScenario);
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
    public void getTransactionsMasterCardWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("757");
        String URI = "/api/transaction/getTransactions";
        String inputInJson = this.mapToJson(request);

        Mockito.when(transactionManagementService.getTransactionsMasterCard(Mockito.any(GetTransactionsRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("757" , responseCode);
    }

    @Test
    public void getTransactionsMasterCardWithException2() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/transaction/getTransactions";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getTransactionsMasterCard(Mockito.any(GetTransactionsRequest.class))).thenThrow(Exception.class);
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
    public void registerWithTDS() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterWithTDSReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/transaction/registerWithTDS";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getRegistrationCode(Mockito.any(GetRegistrationCodeReq.class))).thenReturn(successScenario);
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
    public void registerWithTDSWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterWithTDSReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("703");
        String URI = "/api/transaction/registerWithTDS";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getRegistrationCode(Mockito.any(GetRegistrationCodeReq.class))).thenThrow(hceActionException);
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
    public void registerWithTDSWithException1() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterWithTDSReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/transaction/registerWithTDS";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getRegistrationCode(Mockito.any(GetRegistrationCodeReq.class))).thenThrow(Exception.class);
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
    public void getTransactionHistoryVisa() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsHistoryReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/transaction/getTransactionHistory";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getTransactionHistoryVisa(Mockito.any(GetTransactionHistoryRequest.class))).thenReturn(successScenario);
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
    public void getTransactionHistoryVisaWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsHistoryReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("728");
        String URI = "/api/transaction/getTransactionHistory";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getTransactionHistoryVisa(Mockito.any(GetTransactionHistoryRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("728" , responseCode);
    }

    @Test
    public void getTransactionHistoryVisaWithException1() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsHistoryReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/transaction/getTransactionHistory";
        String inputInJson = this.mapToJson(request);
        Mockito.when(transactionManagementService.getTransactionHistoryVisa(Mockito.any(GetTransactionHistoryRequest.class))).thenThrow(Exception.class);
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

