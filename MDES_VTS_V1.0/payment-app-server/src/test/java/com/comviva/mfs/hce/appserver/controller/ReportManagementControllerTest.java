package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.AddCardParm;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConsumerReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserDeviceCardReportReq;
import com.comviva.mfs.hce.appserver.service.contract.ReportsService;
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
 * Created by rishikesh.kumar on 30-01-2019.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReportManagementControllerTest {

    @MockBean
    private ReportsService reportsService;
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");
    }

    @Test
    public void consumerReport() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/consumerReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/reports/consumerReport";
        String inputInJson = this.mapToJson(request);
        Mockito.when(reportsService.consumerReport(Mockito.any(ConsumerReportReq.class))).thenReturn(successScenario);
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
    public void consumerReportWithInsufficientData() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/consumerReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/reports/consumerReport";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("300");
        Mockito.when(reportsService.consumerReport(Mockito.any(ConsumerReportReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("300" , responseCode);
    }

    @Test
    public void consumerReportWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/consumerReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/reports/consumerReport";
        String inputInJson = this.mapToJson(request);
        Mockito.when(reportsService.consumerReport(Mockito.any(ConsumerReportReq.class))).thenThrow(Exception.class);
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
    public void deviceReport() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/reports/deviceReport";
        String inputInJson = this.mapToJson(request);
        Mockito.when(reportsService.deviceReport(Mockito.any(DeviceReportReq.class))).thenReturn(successScenario);
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
    public void deviceReportWithInsufficientData() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/reports/deviceReport";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("300");
        Mockito.when(reportsService.deviceReport(Mockito.any(DeviceReportReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("300" , responseCode);
    }

    @Test
    public void deviceReportWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/reports/deviceReport";
        String inputInJson = this.mapToJson(request);
        Mockito.when(reportsService.deviceReport(Mockito.any(DeviceReportReq.class))).thenThrow(Exception.class);
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
    public void userDeviceCardMappingReport() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userDeviceCardReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/reports/userDeviceCardMappingReport";
        String inputInJson = this.mapToJson(request);
        Mockito.when(reportsService.userDeviceCardReport(Mockito.any(UserDeviceCardReportReq.class))).thenReturn(successScenario);
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
    public void UserDeviceCardReportWithInsufficientData() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/reports/userDeviceCardMappingReport";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("300");
        Mockito.when(reportsService.userDeviceCardReport(Mockito.any(UserDeviceCardReportReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("300" , responseCode);
    }

    @Test
    public void UserDeviceCardReportWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/reports/userDeviceCardMappingReport";
        String inputInJson = this.mapToJson(request);
        Mockito.when(reportsService.userDeviceCardReport(Mockito.any(UserDeviceCardReportReq.class))).thenThrow(Exception.class);
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
