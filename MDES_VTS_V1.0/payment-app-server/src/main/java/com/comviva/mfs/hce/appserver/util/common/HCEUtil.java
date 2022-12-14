/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.repository.SysMessageRepository;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * Created by shadab.ali on 22-08-2017.
 */
@Getter
@Setter
public class HCEUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCEControllerSupport.class);
    private static SecureRandom secureRandom;
    private static final String ALPHA_NUMERIC_CHARACTERS= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom srnd;

    @Autowired
    private SysMessageRepository sysMessageRepository;

    protected static List<String> maskingPropertiesList = new ArrayList<>();

    static {

        if (HCEConstants.getMaskingProperties() != null && !HCEConstants.getMaskingProperties().isEmpty()) {
            String[] maskingProperties = null;
            maskingProperties =HCEConstants.getMaskingProperties().split(",");
            if (maskingProperties != null && maskingProperties.length != 0) {
                maskingPropertiesList = Arrays.asList(maskingProperties);
            }
        }
    }

//    public static Map<String,Object> formResponse(String messageCode ,String message){
//
//        Map<String,Object> responseMap = null;
//
//
//        return responseMap;
//    }

    /**
     * Locale need to be implemented
     * @return
     */
    public static String getLocale(){

        return HCEConstants.DEFAULT_LANGAUAGE_CODE;
    }

    /**
     * convertDateToTimeStamp
     * @param date date
     * @return Timestamp
     * */
    public static Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }


    /**
     * writeHCELog
     * @param request request
     * @param methodname methodname
     * @param requestId requestId
     * @param response response
     * @param responseCode responseCode
     * @param totalTime totalTime
     * */
    public static void writeHCELog(long totalTime, String responseCode, String requestId,String request,
                                   String response,String methodname) {

        final Logger LOGGER = LoggerFactory.getLogger("requestLogs");

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
        if (methodname != null && !"".equals(methodname)) {
            logMessage.append(methodname);
        }
        logMessage.append("|");
        if (request != null && !"".equals(request)) {
            logMessage.append(maskJson(request));
        }
        logMessage.append("|");
        if (response != null && !"".equals(response)) {
            logMessage.append(maskJson(response));
        }
        logMessage.append("|");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(logMessage.toString());
        } else if (LOGGER.isInfoEnabled()) {
            LOGGER.info(logMessage.toString());
        }
    }


    /**
     * writeTdrLog
     * @param request request
     * @param methodName methodName
     * @param requestId requestId
     * @param response response
     * @param responseCode responseCode
     * @param totalTime totalTime
     * */
    public static void writeTdrLog(long totalTime, String responseCode, String requestId,String request,
                                   String response,String methodName) {

        final Logger LOGGER = LoggerFactory.getLogger("tdrLogs");

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
        if (methodName != null && !"".equals(methodName)) {
            logMessage.append(methodName);
        }
        logMessage.append("|");
        if (request != null && !"".equals(maskJson(request))) {
            logMessage.append(request);
        }
        logMessage.append("|");
        if (response != null && !"".equals(maskJson(response))) {
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

    public static String generateRandomId(String prefix) {
        final int twelve = 12;
        return prefix + (new SimpleDateFormat("yyMMdd").format(new Date()))+ (new SimpleDateFormat("HHmm").format(new Date()))
                + generateAlphanumericRandomNumber(twelve);
    }

    /**
     * generateAlphanumbericRandomNumber
     * @param length length
     * @return stirng
     * */
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
        final int ten = 10;
        ramdongen = secureRandom.nextInt((int) Math.pow(ten, power));

        String strRan = "" + ramdongen;
        while (strRan.length() < power) {
            ramdongen = secureRandom.nextInt((int) Math.pow(ten, power));
            strRan = "" + ramdongen;
        }
        LOGGER.info("generateRandomValue Exit....");
        return ramdongen;

    }

    public SysMessageRepository getSysMessageRepository(){
        return sysMessageRepository;
    }

    /**
     * setSysMessageRepository
     * @param sysMessageRepository sysMessageRepository
     * */
    public void setSysMessageRepository(SysMessageRepository sysMessageRepository){
        this.sysMessageRepository = sysMessageRepository;
    }


    /**
     * Write tdr file log.
     *
     * @param totalTime
     *            the total time
     * @param transactionId
     *            the txn status
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public static void writeTDRFileLog(long totalTime, String transactionId,String request, String response) {
        StringBuilder logMessage = null;

        logMessage = new StringBuilder();
        logMessage.append(totalTime + "|");
        logMessage.append((transactionId != null && !"".equals(transactionId)) ? "TRANSACTION_ID ="+ transactionId: "TRANSACTION_ID =" + HCEConstants.NOT_AVAILABLE);
        logMessage.append((request != null && !"".equals(request)) ? "|REQUEST ="+ request : "|REQUEST =" + HCEConstants.NOT_AVAILABLE);
        logMessage.append((response != null && !"".equals(response)) ? "|RESPONSE="+ response : "|RESPONSE =" + HCEConstants.NOT_AVAILABLE);

        if (LOGGER.isDebugEnabled() || LOGGER.isInfoEnabled()) {
            LOGGER.info(logMessage.toString());
        }
    }



    /**
     * This is java doc.
     *
     * @param jsonData the json data
     * @return the string
     */
    public static String maskJson(String jsonData) {
        if (jsonData.toString().contains("END CERTIFICATE")) {
            return jsonData;
        } else {
            String replaceText = "";
            String[] replaceValue = null;
            String temp = "";
            String original = "";
            Matcher matcher = null;
            String[] node = null;
            String splitParam = null;
            splitParam = ":";
            String patternString = "";
            String tempReplaceValue = "";

            jsonData = formatJsonString(jsonData);

            if (HCEUtil.getMaskingPropertiesList() != null && !HCEUtil.getMaskingPropertiesList().isEmpty()) {

                for (String maskingProp : HCEUtil.getMaskingPropertiesList()) {

                    node = maskingProp.split(":");
                    if (jsonData.contains("\"")) {
                        patternString = "\"" + node[0] + "\"" + splitParam + " \"" + HCEConstants.getMaskingParamRegex() + "\"";
                    } else {
                        patternString = node[0] + splitParam + " " + HCEConstants.getMaskingParamRegex();
                    }

                    matcher = Pattern.compile(patternString).matcher(jsonData);

                    while (matcher.find()) {
                        replaceText = matcher.group();
                        original = replaceText;
                        replaceValue = replaceText.split(splitParam);
                        if (replaceValue != null && replaceValue.length > 1) {

                            final String extValue = replaceValue[1].trim();
                            if (extValue.startsWith("\"") && extValue.endsWith("\"")) {
                                tempReplaceValue = extValue.substring(1, extValue.length() - 1);
                                replaceText = getMaskedValue(tempReplaceValue, node[1], node[2]);
                                replaceText = "\"" + replaceText + "\"";
                            } else {
                                replaceText = getMaskedValue(extValue, node[1], node[2]);
                            }


                            temp = replaceValue[0] + splitParam + replaceText;
                            jsonData = jsonData.replace(original, temp);
                        }

                    }
                }
            }

            return jsonData;
        }
    }

    /**
     * Format Json String
     *
     * @param jsonString
     * @return string
     */
    public static String formatJsonString(String jsonString){
        JsonParser parse = null;
        JsonObject json = null;
        JsonArray jsonArray = null;
        Gson gson = null;
        try{
            parse = new JsonParser();

            JsonElement js = parse.parse(jsonString);
            gson = new GsonBuilder().setPrettyPrinting().create();
            if (js instanceof JsonObject) {
                json = js.getAsJsonObject();
            } else if (js instanceof JsonArray) {
                jsonArray =  js.getAsJsonArray();
                return gson.toJson(jsonArray);
            }

            return gson.toJson(json);

        }catch(Exception e){
            LOGGER.error("Error occured in formatJsonString method of OpenServiceUtil", e);
            return jsonString;
        }
    }





    /**
     * This method mask the number .
     *
     * @param p_value the p_value
     * @param maskingLength the masking length
     * @param p_maskType the p_mask type
     * @return the masked value
     */
    public static String getMaskedValue(String p_value,String maskingLength,String p_maskType)
    {
        String maskedValue=null;

        int len=0;
        if (null!=p_value) {
            len = p_value.length();
        }
        StringBuilder masked=null;
        masked=new StringBuilder();
        int p_digit = 0;
        int maskingLengthInt = 0;

        if("?".equals(maskingLength)){
            if (null!=p_value){
                p_digit = p_value.length();
            }
        }else if(HCEConstants.MASK_TYPE_POST.equals(p_maskType) || HCEConstants.MASK_TYPE_PRE.equals(p_maskType)){
            maskingLengthInt = Integer.parseInt(maskingLength);
            p_digit = maskingLengthInt;
        } else{
            maskingLengthInt = Integer.parseInt(maskingLength);
            if (null!=p_value) {
                p_digit = p_value.length() - maskingLengthInt;
            }

        }
        //  if(HCEConstants.UNMASK_TYPE_PRE.equals())
        // generate no of XXXXs
        for(int i=0;i<p_digit;i++)
            masked.append("X");

        if(p_value!=null && !p_value.isEmpty() ){
            if(len == p_digit){
                maskedValue=masked.toString();
            }else{
                // masking from start position
                if(HCEConstants.MASK_TYPE_PRE.equals(p_maskType) ){
                    maskedValue=p_value.substring(p_digit,len);
                    maskedValue=masked.toString() + maskedValue;
                }else if(HCEConstants.UNMASK_TYPE_PRE.equals(p_maskType)){
                    maskedValue=p_value.substring(0,maskingLengthInt);
                    maskedValue=maskedValue + masked.toString() ;

                }else if(HCEConstants.UNMASK_TYPE_POST.equals(p_maskType)){
                    maskedValue=p_value.substring(len-maskingLengthInt-1,len-1);
                    maskedValue=masked.toString()+maskedValue;

                }else{
                    maskedValue=p_value.substring(0,(len-p_digit));
                    maskedValue=maskedValue + masked.toString() ;
                }
            }

        }
        return maskedValue;
    }



    public static List<String> getMaskingPropertiesList() {
        return maskingPropertiesList;
    }

    /**
     * setMaskingPropertiesList
     * @param maskingPropertiesList maskingPropertiesList
     * */
    public static void setMaskingPropertiesList(
            List<String> maskingPropertiesList) {
        HCEUtil.maskingPropertiesList = maskingPropertiesList;
    }

}
