package com.comviva.mfs.hce.appserver.interceptors;

/**
 * Created by shadab.ali on 29-08-2017.
 */

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor  extends HandlerInterceptorAdapter{

    private static final Logger logger =  LoggerFactory.getLogger(RequestInterceptor.class);


    @Autowired
    HCEControllerSupport hceControllerSupport;
    @Autowired
    private Environment env;

    //before the actual handler will be executed
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(HCEConstants.START_TIME, startTime);

        return true;
    }

    //after the handler is executed
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView)
            throws Exception {

        String requestObject = (String)request.getAttribute(HCEConstants.REQUEST_OBJECT);
        String responseObject = (String)request.getAttribute(HCEConstants.RESPONSE_OBJECT);
        String responseCode = (String) request.getAttribute(HCEConstants.RESPONSE_CODE);
        String url = request.getRequestURI();

        if(HCEConstants.ACTIVE.equals(env.getProperty("audit.trail.required"))){
            hceControllerSupport.maintainAudiTrail(null,url,responseCode,requestObject,responseObject);
        }


        long startTime = (Long)request.getAttribute(HCEConstants.START_TIME);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        HCEUtil.writeHCELog(totalTime,responseCode,null,requestObject,responseObject);
    }

}
