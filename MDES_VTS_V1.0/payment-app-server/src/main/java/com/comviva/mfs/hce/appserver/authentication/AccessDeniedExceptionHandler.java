/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p/>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p/>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.authentication;

import com.comviva.mfs.hce.appserver.mapper.error.ErrorDescription;
import com.comviva.mfs.hce.appserver.mapper.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AccessDeniedExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedExceptionHandler.class);

    /**
     * handleException
     * @param accessDeniedException Access denied exception
     * @param request Request
     * @param response http servlet Response
     * */
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleException(AccessDeniedException accessDeniedException, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.debug("Forbidden access", accessDeniedException);
        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(Arrays.asList(new ErrorDescription("httpErrors.403", accessDeniedException.getMessage()))));
    }
}
