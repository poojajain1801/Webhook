package com.comviva.mfs.promotion.modules.mobilepaymentapi.controller;

import com.comviva.mfs.promotion.Application;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.Constants.KeyConstants;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.Constants.Reqconstants;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.RemoteManagementUtil;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract.RemoteManagementServiceApi;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.AESUtil;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.DefaultTemplateUtils;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.ServiceUtils;
import com.comviva.mfs.promotion.modules.mpamanagement.controller.MpaManagementControllerTest;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.logging.Logger;

import static com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.ServiceUtils.assertResponse;
import static org.junit.Assert.*;

/**
 * Created by tanmay.patel on 4/24/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MobilePaymentApiControllerTest {
    String paymentAppInstanceId="";
    Map regDeviceReaponse=null;
    String tokenUniqueReference ="";

    @Autowired
    RemoteManagementUtil remoteManagementUtil;

    @Autowired
    private RemoteManagementServiceApi remoteManagementServiceImplApi;

    @Resource
    private WebApplicationContext webApplicationContext;



    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/mdes/");

    }
    @Test
    public void registerDevice_ShouldSucceed() throws Exception {
        paymentAppInstanceId = DefaultTemplateUtils.randomString(8);
        Map request = DefaultTemplateUtils.buildRequest("/register_device.json");
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        regDeviceReaponse = ServiceUtils.servicePOSTResponse("mpamanagement/1/0/register",request);;
        assertResponse(regDeviceReaponse, "200");

    }

    @Test
    public void registerDevice_ShouldSucceed_Without_Pin() throws Exception {
        paymentAppInstanceId = DefaultTemplateUtils.randomString(8);
        Map request = DefaultTemplateUtils.buildRequest("/register_device.json");
        request.remove("newMobilePin");
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        regDeviceReaponse = ServiceUtils.servicePOSTResponse("mpamanagement/1/0/register",request);;
        assertResponse(regDeviceReaponse, "200");

    }

   /* public Map postRegisterDevice(String paymentAppInstanceId)
    {
        Map request = DefaultTemplateUtils.buildRequest("/register_device.json");
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        return ServiceUtils.servicePOSTResponse("mpamanagement/1/0/register",request);

    }*/
    @Test
    public void requestSession__ShouldSucceed() throws Exception {
        //Call RegisterDevice
        registerDevice_ShouldSucceed();
        requestSession();

    }
    private void requestSession()
    {
        Map request = DefaultTemplateUtils.buildRequest("/request_session_req.json");
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mobileKeysetId",regDeviceReaponse.get("mobileKeysetId"));
        Map requestSessionResponse = ServiceUtils.servicePOSTResponse("/paymentapp/1/0/requestSession",request);
        System.out.println("requestSessionResponse="+requestSessionResponse);
        assertResponse(requestSessionResponse, "200");
    }

    @Test
    public void provision_ShouldSucceed() throws Exception {
        //Call RegisterDevice
        registerDevice_ShouldSucceed();
        //Call provisonFromMDES
        provisionMdes_ShouldSucceed();
        //call request Session
        requestSession();

        AESUtil aesUtil = new AESUtil();
        String mobileKeySetId =remoteManagementUtil.getMobilKeySetID(paymentAppInstanceId);
        String sessionkeyConf = remoteManagementUtil.getConfSessionKey(paymentAppInstanceId);
        String sessionKeykeyMac = remoteManagementUtil.getMacSessionKey(paymentAppInstanceId);
        String authCode = remoteManagementUtil.getGetAuthCode(paymentAppInstanceId);
       // tokenUniqueReference = remoteManagementServiceImplApi.getTokenUniqueRef(paymentAppInstanceId);
        int m2c = remoteManagementUtil.getM2CCounter(paymentAppInstanceId);
        m2c++;
        String encryptedData=aesUtil.encProvisionMPAReq(m2c,tokenUniqueReference,sessionkeyConf,sessionKeykeyMac);
        Map request = DefaultTemplateUtils.buildRequest("/remoteManagementReq.json");
        request.put("mobileKeysetId",mobileKeySetId);
        request.put("authenticationCode",authCode);
        request.put("encryptedData",encryptedData);

        Map provisionResponse = ServiceUtils.servicePOSTResponse("/paymentapp/1/0/provision",request);
        System.out.println("provisionResponse="+provisionResponse);
        assertResponse(provisionResponse, "200");

    }


    @Test
    public void notifyProvisionResult() throws Exception {
        //
        //Call RegisterDevice
        registerDevice_ShouldSucceed();
        //Call provisonFromMDES
        provisionMdes_ShouldSucceed();
        //call request Session
        requestSession();
        //Call provison api
        provision_ShouldSucceed();

        AESUtil aesUtil = new AESUtil();
        String mobileKeySetId =remoteManagementUtil.getMobilKeySetID(paymentAppInstanceId);
        String sessionkeyConf = remoteManagementUtil.getConfSessionKey(paymentAppInstanceId);
        String sessionKeykeyMac = remoteManagementUtil.getMacSessionKey(paymentAppInstanceId);
        String authCode = remoteManagementUtil.getGetAuthCode(paymentAppInstanceId);
        // tokenUniqueReference = remoteManagementServiceImplApi.getTokenUniqueRef(paymentAppInstanceId);
        int m2c = remoteManagementUtil.getM2CCounter(paymentAppInstanceId);
        m2c++;
        String encryptedData=aesUtil.encNotifyProvisionResultCmsReq(m2c,tokenUniqueReference,sessionkeyConf,sessionKeykeyMac);
        Map request = DefaultTemplateUtils.buildRequest("/remoteManagementReq.json");
        request.put("mobileKeysetId",mobileKeySetId);
        request.put("authenticationCode",authCode);
        request.put("encryptedData",encryptedData);

        Map notifyProvisionResultResp = ServiceUtils.servicePOSTResponse("/paymentapp/1/0/notifyProvisioningResult",request);
        System.out.println("provisionResponse="+notifyProvisionResultResp);
        assertResponse(notifyProvisionResultResp, "200");


    }

    @Test
    public void deleteToken() throws Exception {

    }

    @Test
    public void replenish_shouldSucced() throws Exception {
        //Call RegisterDevice
        registerDevice_ShouldSucceed();
        //Call provisonFromMDES
        provisionMdes_ShouldSucceed();
        //call request Session
        requestSession();
        //Call provison api
        provision_ShouldSucceed();
        //call notify result should succeded.
        notifyProvisionResult();

        AESUtil aesUtil = new AESUtil();
        String mobileKeySetId =remoteManagementUtil.getMobilKeySetID(paymentAppInstanceId);
        String sessionkeyConf = remoteManagementUtil.getConfSessionKey(paymentAppInstanceId);
        String sessionKeykeyMac = remoteManagementUtil.getMacSessionKey(paymentAppInstanceId);
        String authCode = remoteManagementUtil.getGetAuthCode(paymentAppInstanceId);
        // tokenUniqueReference = remoteManagementServiceImplApi.getTokenUniqueRef(paymentAppInstanceId);
        int m2c = remoteManagementUtil.getM2CCounter(paymentAppInstanceId);
        m2c++;
        JSONObject rephineshRequest = new JSONObject( Reqconstants.rephineshRequest);
        rephineshRequest.put("tokenUniqueReference",tokenUniqueReference);
        String encryptedData=aesUtil.prepareEncReq(m2c,rephineshRequest,sessionkeyConf,sessionKeykeyMac);
        Map request = DefaultTemplateUtils.buildRequest("/remoteManagementReq.json");
        request.put("mobileKeysetId",mobileKeySetId);
        request.put("authenticationCode",authCode);
        request.put("encryptedData",encryptedData);

        Map notifyProvisionResultResp = ServiceUtils.servicePOSTResponse("/paymentapp/1/0/replenish",request);
        System.out.println("provisionResponse="+notifyProvisionResultResp);
        assertResponse(notifyProvisionResultResp, "200");
    }




    @Test
    public void ChangeMobilePin_ShouldSucceed() throws Exception {
        //Call RegisterDevice
        registerDevice_ShouldSucceed();
        //call request Session
        requestSession();

        AESUtil aesUtil = new AESUtil();
        String mobileKeySetId =remoteManagementUtil.getMobilKeySetID(paymentAppInstanceId);
        String sessionkeyConf = remoteManagementUtil.getConfSessionKey(paymentAppInstanceId);
        String sessionKeykeyMac = remoteManagementUtil.getMacSessionKey(paymentAppInstanceId);
        String authCode = remoteManagementUtil.getGetAuthCode(paymentAppInstanceId);
        // tokenUniqueReference = remoteManagementServiceImplApi.getTokenUniqueRef(paymentAppInstanceId);
        int m2c = remoteManagementUtil.getM2CCounter(paymentAppInstanceId);
        m2c++;

        JSONObject changeOrsetMobilePin = new JSONObject( Reqconstants.changeMobilePin);
        String dataEncryptionKey = remoteManagementUtil.getDataEncryptionKey(mobileKeySetId);

        //Encrypt new mobile pin and set it in the request.
        String newMobilePinBlock = changeOrsetMobilePin.getString("newMobilePin");
        newMobilePinBlock = aesUtil.encryptData(newMobilePinBlock, dataEncryptionKey);
        changeOrsetMobilePin.put("newMobilePin",newMobilePinBlock);

        //Encrypt Current Mobile pin and sset it in the request.
        String currentMobilePinBlock = changeOrsetMobilePin.getString("currentMobilePin");
        currentMobilePinBlock = aesUtil.encryptData(currentMobilePinBlock, dataEncryptionKey);
        changeOrsetMobilePin.put("currentMobilePin",currentMobilePinBlock);

        String encryptedData=aesUtil.prepareEncReq(m2c,changeOrsetMobilePin,sessionkeyConf,sessionKeykeyMac);
        Map request = DefaultTemplateUtils.buildRequest("/remoteManagementReq.json");
        request.put("mobileKeysetId",mobileKeySetId);
        request.put("authenticationCode",authCode);
        request.put("encryptedData",encryptedData);

        Map changeMobilePinResponse = ServiceUtils.servicePOSTResponse("/paymentapp/1/0/setOrChangeMpin",request);
        System.out.println("changeMobilePinResponse="+changeMobilePinResponse);
        assertResponse(changeMobilePinResponse, "200");
    }
    @Test
    public void setMobilePin_ShouldSucceed() throws Exception {
        //Call RegisterDevice
        registerDevice_ShouldSucceed_Without_Pin();

        //call request Session
        requestSession();

        AESUtil aesUtil = new AESUtil();
        String mobileKeySetId =remoteManagementUtil.getMobilKeySetID(paymentAppInstanceId);
        String sessionkeyConf = remoteManagementUtil.getConfSessionKey(paymentAppInstanceId);
        String sessionKeykeyMac = remoteManagementUtil.getMacSessionKey(paymentAppInstanceId);
        String authCode = remoteManagementUtil.getGetAuthCode(paymentAppInstanceId);
        // tokenUniqueReference = remoteManagementServiceImplApi.getTokenUniqueRef(paymentAppInstanceId);
        int m2c = remoteManagementUtil.getM2CCounter(paymentAppInstanceId);
        m2c++;

        JSONObject changeOrsetMobilePin = new JSONObject( Reqconstants.setPinReq);
        String dataEncryptionKey = remoteManagementUtil.getDataEncryptionKey(mobileKeySetId);

        //Encrypt new mobile pin and set it in the request.
        String newMobilePinBlock = changeOrsetMobilePin.getString("newMobilePin");
        newMobilePinBlock = aesUtil.encryptData(newMobilePinBlock, dataEncryptionKey);
        changeOrsetMobilePin.put("newMobilePin",newMobilePinBlock);

        String encryptedData=aesUtil.prepareEncReq(m2c,changeOrsetMobilePin,sessionkeyConf,sessionKeykeyMac);
        Map request = DefaultTemplateUtils.buildRequest("/remoteManagementReq.json");
        request.put("mobileKeysetId",mobileKeySetId);
        request.put("authenticationCode",authCode);
        request.put("encryptedData",encryptedData);

        Map changeMobilePinResponse = ServiceUtils.servicePOSTResponse("/paymentapp/1/0/setOrChangeMpin",request);
        System.out.println("changeMobilePinResponse="+changeMobilePinResponse);
        assertResponse(changeMobilePinResponse, "200");
    }

    /**
     * @throws Exception
     */
    @Test
    public void provisionMdes_ShouldSucceed() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provision_for_mdes_req.json");
        request.put("encryptedData",AESUtil.encTokenCredentials());
        tokenUniqueReference = DefaultTemplateUtils.randomString(48);
        request.put("tokenUniqueReference",tokenUniqueReference);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        Map response =ServiceUtils.servicePOSTResponse("credentials/1/0/provision",request);
        assertResponse(response, "200");
    }


}