package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.repository.CommonRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.Map;

/**
 * Created by shadab.ali on 22-08-2017.
 */
@Getter
@Setter
public class HCEUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCEControllerSupport.class);
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
        //JSONObject job = null;
        //job=new JSONObject(responseMap);
        //response = job.toString();
        Gson gson = new Gson() ;
        response = gson.toJson(responseMap);
        return response;
    }

    public CommonRepository getCommonRepository(){
        return commonRepository;
    }

    public void setCommonRepository(CommonRepository commonRepository){
        this.commonRepository = commonRepository;
    }
}
