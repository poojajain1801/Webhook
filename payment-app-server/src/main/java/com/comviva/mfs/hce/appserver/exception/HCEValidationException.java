package com.comviva.mfs.hce.appserver.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by shadab.ali on 12-09-2017.
 */
public class HCEValidationException  extends RuntimeException {

    private final String messageCode;
    private final String message;



    /**
     * method to get the messageCode
     * @return messageCode string
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * method to get the messageCode
     * @return messageCode string
     */
    public String getMessage() {
        return message;
    }

    /**
     * constructor of the class
     * @param messageCode messageCode
     *@param messageCode message
     */
    public HCEValidationException(String messageCode,String message) {
        this.messageCode = messageCode ;
        this.message = message;
    }


}
