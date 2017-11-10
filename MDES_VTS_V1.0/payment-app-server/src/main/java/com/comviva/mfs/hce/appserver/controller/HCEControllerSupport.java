package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.model.AuditTrail;
import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.model.SysMessagePK;
import com.comviva.mfs.hce.appserver.repository.AuditTrailRepository;
import com.comviva.mfs.hce.appserver.repository.CommonRepository;
import com.comviva.mfs.hce.appserver.service.UserDetailServiceImpl;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

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
    @Autowired
    private AuditTrailRepository auditTrailRepository;

    public  Map<String,Object> formResponse(String messageCode){

        Map<String,Object> responseMap = new HashMap<String,Object>();

        responseMap.put(HCEConstants.RESPONSE_CODE, messageCode);
        responseMap.put(HCEConstants.MESSAGE,(String)prepareMessage(messageCode));
        return responseMap;
    }

    public  Map<String,Object> formResponse(String messageCode,String message){
        Map<String,Object> responseMap = new HashMap<String ,Object>();
        responseMap.put(HCEConstants.RESPONSE_CODE, messageCode);
        if(message!=null && !message.isEmpty()){
            responseMap.put(HCEConstants.MESSAGE,message);
        }else{
            responseMap.put(HCEConstants.MESSAGE,(String)prepareMessage(messageCode));
        }
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

    public  Object requestFormation(String requestObj ,Class<?>... groups) throws  Exception{

        Object obj = null;
        Class requestClass = null;
        StringBuffer errorMessage = null;
        String errMsg = null;

        try{

            LOGGER.debug("Enter in HCEControllerSupport->requestFormation");
            ObjectMapper mapper = new ObjectMapper();
            obj =mapper.readValue(requestObj,groups[0]);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);

            if (!constraintViolations.isEmpty()) {
                errorMessage = new StringBuffer();
                Iterator iter =null;
                iter=constraintViolations.iterator();
                while (iter.hasNext()) {
                    ConstraintViolation arr = null;
                    arr=(ConstraintViolation) iter.next();
                    arr.getConstraintDescriptor();
                    errorMessage.append("Invalid Property Name:"+ arr.getPropertyPath()+":"+arr.getMessage());
                    errorMessage.append("\n");
                }
            }
            if (errorMessage != null && !"".equals(errorMessage)) {
                errMsg = errorMessage.toString();
                throw new HCEValidationException(HCEMessageCodes.INVALID_PROPERTY,errMsg);
            }

            LOGGER.debug("Exit in HCEControllerSupport->requestFormation");
        }catch (HCEValidationException reqValidationException){
            LOGGER.error("Exception occured in HCEControllerSupport->requestFormation", reqValidationException);
            throw reqValidationException;
        }catch (Exception reqValidationException ){
            LOGGER.error("Exception occured in HCEControllerSupport->requestFormation", reqValidationException);
            throw new HCEActionException(HCEMessageCodes.UNABLE_TO_PARSE_REQUEST);
        }

        return obj;

    }

    /**
     * Locale need to be implemented
     * @return
     */
    public static String getLocale(){

        return HCEConstants.DEFAULT_LANGAUAGE_CODE;
    }

    public void prepareRequest(String request, Map<String,Object> response, HttpServletRequest servletRequest){
        servletRequest.setAttribute(HCEConstants.REQUEST_OBJECT,request);
        servletRequest.setAttribute(HCEConstants.RESPONSE_OBJECT, HCEUtil.getJsonStringFromMap(response));
        servletRequest.setAttribute(HCEConstants.RESPONSE_CODE,response.get(HCEConstants.RESPONSE_CODE));
    }


    public  void maintainAudiTrail(String userId, String url, String responseCode,String request,
                                               String response){
        AuditTrail auditTrail = null;
        try{
            LOGGER.debug("Enter HCEControllerSupport->maintainAudiTrail");
            auditTrail = new AuditTrail();
            auditTrail.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
            if(userId!=null && !userId.isEmpty()){
                auditTrail.setCreatedBy(userId);
            }
            if(responseCode!=null && !responseCode.isEmpty()){
                auditTrail.setResponseCode(responseCode);
            }
            if(request!=null && !request.isEmpty()){
                auditTrail.setRequest(request.getBytes());
            }
            if(response!=null && !response.isEmpty()){
                auditTrail.setResponse(response.getBytes());
            }
            if(url!=null && !url.isEmpty()){
                auditTrail.setServiceType(url);
            }
            auditTrailRepository.save(auditTrail);
            LOGGER.debug("Exit HCEControllerSupport->maintainAudiTrail");

        }catch (Exception e){
            LOGGER.error("Exception occured in HCEControllerSupport->maintainAudiTrail"+e);
        }
    }
}
