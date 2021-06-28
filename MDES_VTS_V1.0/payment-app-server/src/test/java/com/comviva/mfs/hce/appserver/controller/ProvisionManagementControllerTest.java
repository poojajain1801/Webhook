package com.comviva.mfs.hce.appserver.controller;
import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActiveAccountManagementReplenishRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmProvisioningRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmReplenishmenRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetStepUpOptionsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ProvisionTokenGivenPanEnrollmentIdRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SubmitIDandVStepupMethodRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ValidateOTPRequest;
import com.comviva.mfs.hce.appserver.service.contract.ProvisionManagementService;
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
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

/**
 * Created by Bibhash.singh on 28-11-2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProvisionManagementControllerTest {
    @MockBean
    private ProvisionManagementService provisionManagementService;
    @Resource
    private WebApplicationContext webApplicationContext;
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/provisionCard");
    }

    @Test
    public void provisionTokenWithPanEnrollmentId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/provisionTokenWithPanEnrollmentId";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.ProvisionTokenGivenPanEnrollmentId(Mockito.any(ProvisionTokenGivenPanEnrollmentIdRequest.class))).thenReturn(successScenario);
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
    public void provisionTokenWithPanEnrollmentIdWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/provisionTokenWithPanEnrollmentId";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");
        Mockito.when(provisionManagementService.ProvisionTokenGivenPanEnrollmentId(Mockito.any(ProvisionTokenGivenPanEnrollmentIdRequest.class))).thenThrow(hceActionException);
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
    public void provisionTokenWithPanEnrollmentIdWithexception() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/provisionTokenWithPanEnrollmentId";
        String inputInJson = this.mapToJson(request);
        Mockito.when(provisionManagementService.ProvisionTokenGivenPanEnrollmentId(Mockito.any(ProvisionTokenGivenPanEnrollmentIdRequest.class))).thenThrow(Exception.class);
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
    public void confirmProvisioning() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/confirmProvisioningReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/confirmProvisioning";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.ConfirmProvisioning(Mockito.any(ConfirmProvisioningRequest.class))).thenReturn(successScenario);
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
    public void confirmProvisioningWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/confirmProvisioningReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/confirmProvisioning";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");
        Mockito.when(provisionManagementService.ConfirmProvisioning(Mockito.any(ConfirmProvisioningRequest.class))).thenThrow(hceActionException);
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
    public void confirmProvisioningWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/confirmProvisioningReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/confirmProvisioning";
        String inputInJson = this.mapToJson(request);
        Mockito.when(provisionManagementService.ConfirmProvisioning(Mockito.any(ConfirmProvisioningRequest.class))).thenThrow(Exception.class);
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
    public void getStepUpOptions() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getStepUpOptionsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/getStepUpOptions";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.getStepUpOptions(Mockito.any(GetStepUpOptionsRequest.class))).thenReturn(successScenario);
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
    @Ignore
    public void getStepUpOptionsWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getStepUpOptionsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/getStepUpOptions";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("768");
        Mockito.when(provisionManagementService.getStepUpOptions(Mockito.any(GetStepUpOptionsRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("768" , responseCode);
    }


    @Test
    public void validateOTP() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/validateOTPReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/validateOTP";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.validateOTP(Mockito.any(ValidateOTPRequest.class))).thenReturn(successScenario);
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
    public void validateOTPWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/validateOTPReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String URI = "/api/provisionCard/validateOTP";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("768");
        Mockito.when(provisionManagementService.validateOTP(Mockito.any(ValidateOTPRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("768" , responseCode);
    }

    @Test
    public void validateOTPWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/validateOTPReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String URI = "/api/provisionCard/validateOTP";
        String inputInJson = this.mapToJson(request);
        Mockito.when(provisionManagementService.validateOTP(Mockito.any(ValidateOTPRequest.class))).thenThrow(Exception.class);
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
    public void activeAccountManagementReplenish() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateAccountManagementReplenishReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/activeAccountManagementReplenish";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.ActiveAccountManagementReplenish(Mockito.any(ActiveAccountManagementReplenishRequest.class))).thenReturn(successScenario);
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
    public void activeAccountManagementReplenishWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateAccountManagementReplenishReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/activeAccountManagementReplenish";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");
        Mockito.when(provisionManagementService.ActiveAccountManagementReplenish(Mockito.any(ActiveAccountManagementReplenishRequest.class))).thenThrow(hceActionException);
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
    public void activeAccountManagementReplenishWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateAccountManagementReplenishReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/activeAccountManagementReplenish";
        String inputInJson = this.mapToJson(request);
        Mockito.when(provisionManagementService.ActiveAccountManagementReplenish(Mockito.any(ActiveAccountManagementReplenishRequest.class))).thenThrow(Exception.class);
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
    public void activeAccountManagementConfirmReplenishment() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activeAccountManagementConfirmReplenishmentRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/activeAccountManagementConfirmReplenishment";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.ActiveAccountManagementConfirmReplenishment(Mockito.any(ConfirmReplenishmenRequest.class))).thenReturn(successScenario);
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
    public void activeAccountManagementConfirmReplenishmentWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activeAccountManagementConfirmReplenishmentRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/activeAccountManagementConfirmReplenishment";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");
        Mockito.when(provisionManagementService.ActiveAccountManagementConfirmReplenishment(Mockito.any(ConfirmReplenishmenRequest.class))).thenThrow(hceActionException);
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
    public void activeAccountManagementConfirmReplenishmentWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activeAccountManagementConfirmReplenishmentRequest.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/activeAccountManagementConfirmReplenishment";
        String inputInJson = this.mapToJson(request);
        Mockito.when(provisionManagementService.ActiveAccountManagementConfirmReplenishment(Mockito.any(ConfirmReplenishmenRequest.class))).thenThrow(Exception.class);
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
    public void submitIDandVStepupMethodRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/submitIDandVStepupMethodReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/provisionCard/submitIDandVStepupMethodRequest";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.submitIDandVStepupMethod(Mockito.any(SubmitIDandVStepupMethodRequest.class))).thenReturn(successScenario);
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
    public void submitIDandVStepupMethodRequestWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/submitIDandVStepupMethodReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("707");
        String URI = "/api/provisionCard/submitIDandVStepupMethodRequest";
        String inputInJson = this.mapToJson(request);

        Mockito.when(provisionManagementService.submitIDandVStepupMethod(Mockito.any(SubmitIDandVStepupMethodRequest.class))).thenThrow(hceActionException);
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
    public void submitIDandVStepupMethodRequestWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/submitIDandVStepupMethodReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/provisionCard/submitIDandVStepupMethodRequest";
        String inputInJson = this.mapToJson(request);
        Mockito.when(provisionManagementService.submitIDandVStepupMethod(Mockito.any(SubmitIDandVStepupMethodRequest.class))).thenThrow(Exception.class);
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

