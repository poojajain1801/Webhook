package com.comviva.mfs.promotion.modules.mobilepaymentapi.service;

import com.comviva.mfs.promotion.modules.common.sessionmanagement.repository.SessionInfoRepository;
import com.comviva.mfs.promotion.modules.common.tokens.repository.TokenRepository;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSession;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSessionResp;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.RemoteManagementUtil;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract.RemoteManagementServiceApi;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by Tanmay.Patel on 4/13/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RemoteManagementServiceImplApiTest {

   /* TokenRepository tokenRepository;
    ApplicationInstanceInfoRepository appInstInfoRepository;
    SessionInfoRepository sessionInfoRepository;
    RemoteManagementUtil remoteManagementUtil;*/

     //RemoteManagementServiceImplApi remoteManagementServiceImplApi = new RemoteManagementServiceImplApi(tokenRepository,appInstInfoRepository,sessionInfoRepository,remoteManagementUtil);
    @Autowired
    private RemoteManagementServiceApi remoteManagementServiceImplApi;

    private RequestSession requestSession = new RequestSession("WalletApp1","123456789","3B363742C1B34CB0862706C46221C816AC381F216B62C15D4400015A80746D4F");
    private RequestSessionResp requestSessionResp;



    @Test
    public void requestSession() throws Exception {

        requestSessionResp = remoteManagementServiceImplApi.requestSession(requestSession);
        System.out.println("Staus = "+requestSessionResp.getReasonCode());
    }

    @Test
    public void provision() throws Exception {

    }

    @Test
    public void notifyProvisioningResult() throws Exception {

    }

    @Test
    public void deleteToken() throws Exception {

    }

    @Test
    public void replenish() throws Exception {

    }

}