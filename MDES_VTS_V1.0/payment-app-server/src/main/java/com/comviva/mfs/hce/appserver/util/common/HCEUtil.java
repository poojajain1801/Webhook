package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.repository.CommonRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shadab.ali on 22-08-2017.
 */
@Getter
@Setter
public class HCEUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCEControllerSupport.class);
    private static SecureRandom secureRandom = null;
    private static final String ALPHA_NUMERIC_CHARACTERS= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom srnd = null;
    @Autowired
    private CommonRepository commonRepository;

    public static Map<String,Object> formResponse(String messageCode ,String message){

        Map<String,Object> responseMap = null;


        return responseMap;
    }

    /**
     * Locale need to be implemented
     * @return
     */
    public static String getLocale(){

        return HCEConstants.DEFAULT_LANGAUAGE_CODE;
    }


    public static Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }


    public static void writeHCELog(long totalTime, String responseCode, String requestId,String request,
                                      String response) {


        StringBuilder logMessage = null;
        logMessage = new StringBuilder();
        logMessage.append(HCEUtil.convertDateToTimestamp(new Date()));
        logMessage.append("|");
        logMessage.append(totalTime);
        logMessage.append("|");
        if (responseCode != null && !"".equals(responseCode)) {
            logMessage.append(responseCode);
        }
        logMessage.append("|");
        if (requestId != null && !"".equals(requestId)) {
            logMessage.append(requestId);
        }
        logMessage.append("|");
        if (request != null && !"".equals(request)) {
            logMessage.append(request);
        }
        logMessage.append("|");
        if (response != null && !"".equals(response)) {
            logMessage.append(response);
        }
        logMessage.append("|");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(logMessage.toString());
        } else if (LOGGER.isInfoEnabled()) {
            LOGGER.info(logMessage.toString());
        }
    }


    /**
     * Gets the json string from map.
     *
     * @param responseMap the response map
     * @return the json string from map
     */
    public static String getJsonStringFromMap(Map<String, Object> responseMap) {
        String response;
        JSONObject job = null;
        job=new JSONObject(responseMap);
        response = job.toString();
        //Gson gson = new Gson() ;
        //response = gson.toJson(responseMap);
        return response;
    }

    public static String generateRandomId(String prefix) throws NoSuchAlgorithmException {
        return prefix + (new SimpleDateFormat("yyMMdd").format(new Date()))+ (new SimpleDateFormat("HHmm").format(new Date()))
                + generateAlphanumericRandomNumber(12);
    }


    public static String generateAlphanumericRandomNumber(int length){

        if(srnd ==null){
            srnd = new SecureRandom();
        }
        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length; i++ )
            sb.append( ALPHA_NUMERIC_CHARACTERS.charAt( srnd.nextInt(ALPHA_NUMERIC_CHARACTERS.length()) ) );
        return sb.toString().toUpperCase();

    }

    /**
     * This method will generate a random integer value by taking double value.
     *
     * @param power
     *            the power
     * @return int
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     */
    public static int generateRandomValue(int power) throws NoSuchAlgorithmException {
        LOGGER.info("generateRandomValue Entered....");

        int seedByteCount = power;
        if (secureRandom == null) {
            SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
            byte[] seed = secureRandomGenerator.generateSeed(seedByteCount);

            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed);
        }
        int ramdongen = 0;

        ramdongen = secureRandom.nextInt((int) Math.pow(10, power));

        String strRan = "" + ramdongen;
        while (strRan.length() < power) {
            ramdongen = secureRandom.nextInt((int) Math.pow(10, power));
            strRan = "" + ramdongen;
        }
        LOGGER.info("generateRandomValue Exit....");
        return ramdongen;

    }

    public CommonRepository getCommonRepository(){
        return commonRepository;
    }

    public void setCommonRepository(CommonRepository commonRepository){
        this.commonRepository = commonRepository;
    }

}
