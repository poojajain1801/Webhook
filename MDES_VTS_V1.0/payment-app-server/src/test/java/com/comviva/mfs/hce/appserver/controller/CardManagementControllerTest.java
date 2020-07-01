package com.comviva.mfs.hce.appserver.controller;
import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONObject;
import org.junit.Assert;
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
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CardManagementControllerTest {

    @MockBean
    private CardDetailService cardDetailService;
    @Resource
    private WebApplicationContext webApplicationContext;
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");
    }

    @Test
    public void addCard() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/checkCardEligibility";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.checkDeviceEligibility(Mockito.any(AddCardParm.class))).thenReturn(successScenario);
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
    public void addCardWhenCardNotEligible() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/checkCardEligibility";
        String inputInJson = this.mapToJson(request);

        HCEActionException hceActionException = new HCEActionException("718");

        Mockito.when(cardDetailService.checkDeviceEligibility(Mockito.any(AddCardParm.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("718" , responseCode);
    }

    @Test
    public void addCardWhenExceptionOccurs() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/checkCardEligibility";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.checkDeviceEligibility(Mockito.any(AddCardParm.class))).thenThrow(Exception.class);
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
    public void enrollPan() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/enrollPan";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.enrollPan(Mockito.any(EnrollPanRequest.class))).thenReturn(successScenario);
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
    public void enrollPanWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/enrollPan";
        String inputInJson = this.mapToJson(request);

        HCEActionException hceActionException = new HCEActionException("207");

        Mockito.when(cardDetailService.enrollPan(Mockito.any(EnrollPanRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("207" , responseCode);
    }

    @Test
    public void enrollPanWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/enrollPan";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.enrollPan(Mockito.any(EnrollPanRequest.class))).thenThrow(Exception.class);
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
    public void enrollPanWithValidationException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/enrollPan";
        String inputInJson = this.mapToJson(request);

        HCEValidationException hceValidationException = new HCEValidationException("207","Validation exception");

        Mockito.when(cardDetailService.enrollPan(Mockito.any(EnrollPanRequest.class))).thenThrow(hceValidationException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("207" , responseCode);
    }

    @Test
    public void continueDigitization() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/continueDigitization";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.addCard(Mockito.any(DigitizationParam.class))).thenReturn(successScenario);
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
    public void continueDigitizationWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/continueDigitization";
        String inputInJson = this.mapToJson(request);

        HCEActionException hceActionException = new HCEActionException("207");

        Mockito.when(cardDetailService.addCard(Mockito.any(DigitizationParam.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("207" , responseCode);
    }

    @Test
    public void continueDigitizationWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/continueDigitization";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.addCard(Mockito.any(DigitizationParam.class))).thenThrow(Exception.class);
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
    public void tokenize() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/tokenize.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/tokenize";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.tokenize(Mockito.any(TokenizeRequest.class))).thenReturn(successScenario);
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
    public void tokenizeWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/tokenize.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/tokenize";
        String inputInJson = this.mapToJson(request);

        HCEActionException hceActionException = new HCEActionException("201");

        Mockito.when(cardDetailService.tokenize(Mockito.any(TokenizeRequest.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("201" , responseCode);
    }

    @Test
    public void tokenizeWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/tokenize.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/tokenize";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.tokenize(Mockito.any(TokenizeRequest.class))).thenThrow(Exception.class);
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
    public void getAsset() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getAssetReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/mdes/asset";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.getAsset(Mockito.any(GetAssetPojo.class))).thenReturn(successScenario);
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
    public void getAssetWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getAssetReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/mdes/asset";
        String inputInJson = this.mapToJson(request);

        HCEActionException hceActionException = new HCEActionException("707");

        Mockito.when(cardDetailService.getAsset(Mockito.any(GetAssetPojo.class))).thenThrow(hceActionException);
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
    public void activate() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/activate";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.activate(Mockito.any(ActivateReq.class))).thenReturn(successScenario);
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
    public void activateWithInvalidCode() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/activate";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("202");

        Mockito.when(cardDetailService.activate(Mockito.any(ActivateReq.class))).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON).content(inputInJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("202" , responseCode);
    }

    @Test
    public void activateWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/activate";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.activate(Mockito.any(ActivateReq.class))).thenThrow(Exception.class);
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
    public void getCardMetadata() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getCardMetaDataReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/getCardMetadata";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.getCardMetadata(Mockito.any(GetCardMetadataRequest.class))).thenReturn(successScenario);
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
    public void getContent() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getContentReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/getContent";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.getContent(Mockito.any(GetContentRequest.class))).thenReturn(successScenario);
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
    public void provisionWithPanEnrollmentID() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        Map getContentResp = ServiceUtils.servicePOSTResponse("provision/provisionTokenWithPanEnrollmentId",request);
        assertResponse(getContentResp, "707");
    }

    @Test
    public void lifeCycleManagement() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/LifeCycleManagementReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/lifeCycleManagement";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.performCardLifeCycleManagement(Mockito.any(LifeCycleManagementReq.class))).thenReturn(successScenario);
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
    public void lifeCycleManagementWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/LifeCycleManagementReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/lifeCycleManagement";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");

        Mockito.when(cardDetailService.performCardLifeCycleManagement(Mockito.any(LifeCycleManagementReq.class))).thenThrow(hceActionException);
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
    public void lifeCycleManagementWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/LifeCycleManagementReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/lifeCycleManagement";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.performCardLifeCycleManagement(Mockito.any(LifeCycleManagementReq.class))).thenThrow(Exception.class);
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
    public void getTokens() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/getToken";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.getTokens(Mockito.any(GetTokensRequest.class))).thenReturn(successScenario);
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
    public void getTokensWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/getToken";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");

        Mockito.when(cardDetailService.getTokens(Mockito.any(GetTokensRequest.class))).thenThrow(hceActionException);
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
    public void getTokensWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/getToken";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.getTokens(Mockito.any(GetTokensRequest.class))).thenThrow(Exception.class);
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
    public void searchTokens() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/searchTokensReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/searchTokens";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.searchTokens(Mockito.any(SearchTokensReq.class))).thenReturn(successScenario);
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
    public void searchTokensWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/searchTokensReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/searchTokens";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");

        Mockito.when(cardDetailService.searchTokens(Mockito.any(SearchTokensReq.class))).thenThrow(hceActionException);
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
    public void searchTokensWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/searchTokensReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/searchTokens";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.searchTokens(Mockito.any(SearchTokensReq.class))).thenThrow(Exception.class);
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
    public void requestActivationCode() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/requestActivationCodeReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/requestActivationCode";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.requestActivationCode(Mockito.any(ActivationCodeReq.class))).thenReturn(successScenario);
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
    public void requestActivationCodeWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/requestActivationCodeReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/requestActivationCode";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("707");

        Mockito.when(cardDetailService.requestActivationCode(Mockito.any(ActivationCodeReq.class))).thenThrow(hceActionException);
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
    public void requestActivationCodeWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/requestActivationCodeReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/requestActivationCode";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.requestActivationCode(Mockito.any(ActivationCodeReq.class))).thenThrow(Exception.class);
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
    public void unregisterFromTds() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/unregisterTds";
        String inputInJson = this.mapToJson(request);

        Mockito.when(cardDetailService.unregisterTds(Mockito.any(UnregisterTdsReq.class))).thenReturn(successScenario);
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
    public void unregisterFromTdsWithActionException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/unregisterTds";
        String inputInJson = this.mapToJson(request);
        HCEActionException hceActionException = new HCEActionException("704");

        Mockito.when(cardDetailService.unregisterTds(Mockito.any(UnregisterTdsReq.class))).thenThrow(hceActionException);
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

    @Test
    public void unregisterFromTdsWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/unregisterTds";
        String inputInJson = this.mapToJson(request);
        Mockito.when(cardDetailService.unregisterTds(Mockito.any(UnregisterTdsReq.class))).thenThrow(Exception.class);
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
    public void getPrivateKeyFromKeyStore() throws Exception {
        HCEControllerSupport hceControllerSupport = new HCEControllerSupport();
        PrivateKey response = null;
        String res = null;
        try {
            response = hceControllerSupport.getPrivateKeyFromKeyStore();
        }catch (Exception e){
            res = e.getMessage();
        }
        Assert.assertNotNull(res);
    }

    @Test
    public void aesDecrypt() throws Exception {
        HCEControllerSupport hceControllerSupport = new HCEControllerSupport();
        String response = null;
        byte[] bKey = new byte[]{1,2,3,4};
        try {
            response = hceControllerSupport.aesDecrypt("rtyulkjhgfdfghjkjhg", bKey, "45670987645");
        }catch (Exception e){
            response = e.getMessage();
        }
        Assert.assertNotNull(response);
    }

    @Test
    public void getSystemHealth() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/getSystemHealth";

        Mockito.when(cardDetailService.getSystemHealth()).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                URI).accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200" , responseCode);
    }

    @Test
    @Ignore
    public void getSystemHealthWithActionException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String URI = "/api/card/getSystemHealth";
        HCEActionException hceActionException = new HCEActionException("262");
        Mockito.when(cardDetailService.getSystemHealth()).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                URI).accept(
                MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("262" , responseCode);
    }

    @Test
    public void getPublicKeyCertificate() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Map<String,Object> successScenario = new HashMap<>();
        successScenario.put("Message","Transaction Success");
        successScenario.put("reasonCode","200");
        String URI = "/api/card/pkCertificate";

        Mockito.when(cardDetailService.getPublicKeyCertificate()).thenReturn(successScenario);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                URI).accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("reasonCode");
        assertEquals("200" , responseCode);
    }

    @Test
    @Ignore
    public void getPublicKeyCertificateWithActionException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        HCEActionException hceActionException = new HCEActionException("704");
        String URI = "/api/card/pkCertificate";

        Mockito.when(cardDetailService.getPublicKeyCertificate()).thenThrow(hceActionException);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                URI).accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        JSONObject jsonObject = new JSONObject(outputInJson);
        String responseCode = jsonObject.getString("responseCode");
        assertEquals("704" , responseCode);
    }

    @Test
    public void decryptRequest() throws Exception {
        HCEControllerSupport hceControllerSupport = new HCEControllerSupport();
        String response = null;
        Map request = DefaultTemplateUtils.buildRequest("/decryptRequest.json");
        String decryptRequest = request.toString();
        try {
            response = hceControllerSupport.decryptRequest(decryptRequest);
        }catch (HCEActionException e){
            response = e.getMessageCode();
        }
        Assert.assertEquals(response , "706");
    }

    @Test
    public void formResponse() throws Exception {
        HCEControllerSupport hceControllerSupport = new HCEControllerSupport();
        Map<String, Object> response = hceControllerSupport.formResponse("200", "Transaction Success");
        Assert.assertNotNull(response);
    }

    @Test
    public void formResponseWithNullMessage() throws Exception {
        HCEControllerSupport hceControllerSupport = new HCEControllerSupport();
        Map<String, Object> response = hceControllerSupport.formResponse("200", null);
        Assert.assertNotNull(response);
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}