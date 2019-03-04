/*******************************************************************************
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.comviva.mfs.hce.appserver.exception;

import com.comviva.mfs.hce.appserver.mapper.validation.DomainValidationUtils;
import lombok.Getter;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import java.util.Map;

@Getter
public class DataConversionException extends RuntimeException {
    private final Errors errors;

    public DataConversionException(Map map, String field, Class<?> aClass, Throwable throwable) {
        super(String.format("Error converting value: %s to %s for field: %s", map.get(field), aClass, field), throwable);
        this.errors = createErrorsFor(map, field);
    }

    public DataConversionException(Map map, String field, Class<?> aClass) {
        this(map, field, aClass, null);
    }

    private Errors createErrorsFor(final Map map, final String field) {
        Errors errors = new MapBindingResult(map, "");
        Object value = map.get(field);
        errors.rejectValue(field, DomainValidationUtils.ErrorCodes.INVALID_VALUE, new Object[]{value, field}, null);
        return errors;
    }
}
