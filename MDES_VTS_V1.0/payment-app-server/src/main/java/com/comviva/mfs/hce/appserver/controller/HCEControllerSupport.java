package com.comviva.mfs.hce.appserver.controller;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import org.apache.commons.codec.binary.Base64;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.model.AuditTrail;
import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.repository.AuditTrailRepository;
import com.comviva.mfs.hce.appserver.repository.CommonRepository;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.io.InputStream;
import org.springframework.core.io.Resource;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
    private UserDetailRepository userDetailRepository;
    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private Environment env;
    private static PrivateKey privateKey;

    public  Map<String,Object> formResponse(String messageCode){

        Map<String,Object> responseMap = new HashMap<>();

        responseMap.put(HCEConstants.RESPONSE_CODE, messageCode);
        responseMap.put(HCEConstants.MESSAGE,(String)prepareMessage(messageCode));
        return responseMap;
    }

    public  Map<String,Object> formResponse(String messageCode,String message){
        Map<String,Object> responseMap = new HashMap<>();
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
        try{

            sysMessageList = commonRepository.find(messageCode,getLocale());
            if(sysMessageList!=null && !sysMessageList.isEmpty()){
                txnMessage = sysMessageList.get(0).getMessage();
            }
        }catch (Exception prepareMessageException){
            LOGGER.error("Exception occured in HCEControllerSupport->prepareMessage", prepareMessageException);
            txnMessage = HCEConstants.INTERNAL_SERVER_ERROR;
        }
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

            if (errorMessage != null && errorMessage.length() !=0) {
                errMsg = errorMessage.toString();
                throw new HCEValidationException(HCEMessageCodes.getInvalidProperty(),errMsg);
            }

            LOGGER.debug("Exit in HCEControllerSupport->requestFormation");
        }catch (HCEValidationException reqValidationException){
            LOGGER.error("Exception occured in HCEControllerSupport->requestFormation", reqValidationException);
            throw reqValidationException;
        }catch (Exception reqValidationException ){
            LOGGER.error("Exception occured in HCEControllerSupport->requestFormation", reqValidationException);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
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


    public  void maintainAudiTrail(String userId,String clientDeviceID, String url, String responseCode,String request,
                                               String response,String totalTime){
        AuditTrail auditTrail = null;
        try{
            LOGGER.debug("Enter HCEControllerSupport->maintainAudiTrail");
            auditTrail = new AuditTrail();
            auditTrail.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
            if(userId!=null && !userId.isEmpty()){
                auditTrail.setCreatedBy(userId);
            }
            if(clientDeviceID!=null && !clientDeviceID.isEmpty()){
                auditTrail.setClientDeviceId(clientDeviceID);
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
            if(totalTime!=null && !totalTime.isEmpty())
            {
                auditTrail.setTotalTimeTaken(totalTime);
            }
            auditTrailRepository.save(auditTrail);
            LOGGER.debug("Exit HCEControllerSupport->maintainAudiTrail");

        }catch (Exception e){
            LOGGER.error("Exception occured in HCEControllerSupport->maintainAudiTrail"+e);
        }
    }


    private  PrivateKey getPrivateKeyFromKeyStore() throws Exception{
        ResourceLoader resourceLoader = null;
        Resource resource = null;
        InputStream inputStream = null;
        String fileName = null;
        try{
            //InputStream ins = DecryptPayload.class.getResourceAsStream("/keystore.
            //
            // jks");

            fileName = env.getProperty("end.to.end.keystore.filename");
            resourceLoader = new FileSystemResourceLoader() ;
            resource = resourceLoader.getResource("classpath:"+fileName);
            inputStream  = resource.getInputStream();

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(inputStream, env.getProperty("end.to.end.keystore.secret.key").toCharArray());   //Keystore password
            KeyStore.PasswordProtection keyPassword =       //Key password
                    new KeyStore.PasswordProtection(env.getProperty("end.to.end.keystore.secret.key").toCharArray());

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(env.getProperty("end.to.end.keystore.alias"), keyPassword);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            return privateKey;
        }catch (Exception ex) {
            LOGGER.error("Error in AESEncrypt getPrivateKeyFromKeyStore : " + ex.getMessage(), ex);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
        }

    }



    private String aesDecrypt(String encryptedText, byte[] bKey,String iv)throws Exception {
        try {
            SecretKey key2 = new SecretKeySpec(bKey, 0, bKey.length, "AES");
            // Instantiate the cipher
            Cipher cipher;
            cipher= Cipher.getInstance("AES/CBC/PKCS5PADDING");
            byte[] ivBytes = new Base64().decode(iv);
            cipher.init(Cipher.DECRYPT_MODE, key2, new IvParameterSpec(ivBytes));
            byte[] encryptedTextBytes = new Base64().decode(encryptedText);
            byte[] decryptedTextBytes ;
            decryptedTextBytes= cipher.doFinal(encryptedTextBytes);
            return new String(decryptedTextBytes);
        } catch (Exception ex) {
            LOGGER.error("Error in AESEncrypt CryptoUtil : " + ex.getMessage(), ex);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
        }

    }


    public String decryptRequest(String request) throws HCEActionException{
        String decryptedData = null;

        try{

            if(HCEConstants.ACTIVE.equals(env.getProperty("enable.end.to.end.encryption"))) {

                JSONObject jsonObject = new JSONObject(request);
                String requestEncKey = (String) jsonObject.get("requestKey");
                String requestIV = (String) jsonObject.get("requestIV");
                String requestEncData = (String) jsonObject.get("requestEncData");

                if (null == requestEncKey || null == requestIV || null == requestEncData || requestEncKey.isEmpty() || requestIV.isEmpty() || requestEncData.isEmpty()) {
                    throw new HCEActionException(HCEMessageCodes.getInsufficientData());
                }
                if (privateKey == null) {
                    privateKey = getPrivateKeyFromKeyStore();
                }
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decData = cipher.doFinal(new Base64().decode(requestEncKey));
                decryptedData = aesDecrypt(requestEncData, decData, requestIV);

            }else{
                decryptedData = request;
            }

            return  decryptedData;

        }catch (HCEActionException decReqActionException){
            LOGGER.error("Exception occured in HCEControllerSupport->decryptRequest ", decReqActionException);
            throw decReqActionException;

        }catch (Exception decReqException){
            LOGGER.error("Exception occured in HCEControllerSupport->decryptRequest ", decReqException);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
        }
    }



    public String findUserId(String clientWalletAccountId){

        final List<UserDetail> userDetails = userDetailRepository.findByClientWalletAccountId(clientWalletAccountId);
        if(userDetails!=null && !userDetails.isEmpty()){
            final  UserDetail userDetail1 = userDetails.get(0);
            return userDetail1.getUserId();
        }else{
            return  clientWalletAccountId;
        }
    }
}
