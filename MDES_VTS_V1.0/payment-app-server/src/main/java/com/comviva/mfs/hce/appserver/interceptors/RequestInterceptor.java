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
package com.comviva.mfs.hce.appserver.interceptors;

/**
 * Created by shadab.ali on 29-08-2017.
 */

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor  extends HandlerInterceptorAdapter{

    @Autowired
    private HCEControllerSupport hceControllerSupport;
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
            hceControllerSupport.maintainAudiTrail(null,null,url,responseCode,requestObject,responseObject,null);
        }

        long startTime = (Long)request.getAttribute(HCEConstants.START_TIME);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        HCEUtil.writeHCELog(totalTime,responseCode,null,requestObject,responseObject,url);
    }

}

