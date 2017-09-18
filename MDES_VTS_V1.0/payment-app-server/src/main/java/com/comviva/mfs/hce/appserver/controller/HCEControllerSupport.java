package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.model.SysMessagePK;
import com.comviva.mfs.hce.appserver.repository.CommonRepository;
import com.comviva.mfs.hce.appserver.service.UserDetailServiceImpl;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by shadab.ali on 23-08-2017.
 */
@Component
@Getter
@Setter
public class HCEControllerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCEControllerSupport.class);
    @Autowired
    private CommonRepository commonRepository;

    public  Map<String,Object> formResponse(String messageCode){

        Map<String,Object> responseMap = new LinkedHashMap<>();

        responseMap.put(HCEConstants.RESPONSE_CODE, messageCode);
        responseMap.put(HCEConstants.MESSAGE,(String)prepareMessage(messageCode));

        return responseMap;
    }


    /**
     *
     * @param messageCode
     * @return
     */
    public String prepareMessage(String messageCode){

        String txnMessage = null;
        List<SysMessage> sysMessageList = null;
        LOGGER.debug("Enter in HCEControllerSupport->prepareMessage");
        try{

            sysMessageList = commonRepository.find(messageCode,getLocale());

            if(sysMessageList!=null && !sysMessageList.isEmpty()){
                txnMessage = sysMessageList.get(0).getMessage();
            }

        }catch (Exception prepareMessageException){
            LOGGER.error("Exception occured in HCEControllerSupport->prepareMessage", prepareMessageException);
            txnMessage = HCEConstants.INTERNAL_SERVER_ERROR;
        }

        LOGGER.debug("Exit in HCEControllerSupport->prepareMessage");

        return txnMessage;

    }

    /**
     * Locale need to be implemented
     * @return
     */
    public static String getLocale(){

        return HCEConstants.DEFAULT_LANGAUAGE_CODE;
    }





}
