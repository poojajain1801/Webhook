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
package com.comviva.mfs.hce.appserver.mapper;

import com.comviva.mfs.hce.appserver.mapper.error.ErrorDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by charu.sharma on 12/29/2016.
 */
@Component
public class ErrorDescriptionMapper {
    private final MessageSource messageSource;

    @Autowired
    public ErrorDescriptionMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public List<ErrorDescription> map(Errors errors, Locale locale) {
        return errors.getAllErrors().stream().map(error -> {
            String field = error instanceof FieldError ? ((FieldError) error).getField() : "";
            String message = messageSource.getMessage(error, locale);
            String errorCode = Arrays.asList(error.getCodes()).stream().filter(code -> !code.contains("[")).findFirst().orElse(error.getCode());
            return new ErrorDescription(field, errorCode, message);
        }).collect(Collectors.toList());
    }
}
