package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.repository.CommonRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Map;

/**
 * Created by shadab.ali on 22-08-2017.
 */
@Getter
@Setter
public class HCEUtil {
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


    public CommonRepository getCommonRepository(){
        return commonRepository;
    }

    public void setCommonRepository(CommonRepository commonRepository){
        this.commonRepository = commonRepository;
    }
}
