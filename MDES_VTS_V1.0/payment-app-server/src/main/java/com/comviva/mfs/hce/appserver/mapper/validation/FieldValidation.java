/*
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
 */
package com.comviva.mfs.hce.appserver.mapper.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.comviva.mfs.hce.appserver.mapper.validation.DomainValidationUtils.ErrorCodes.*;

public class FieldValidation {
    private final ValidationDslBuilder validationDslBuilder;
    private final String lhsFieldName;

    public FieldValidation(ValidationDslBuilder validationDslBuilder, String lhsFieldName) {
        this.validationDslBuilder = validationDslBuilder;
        this.lhsFieldName = lhsFieldName;
    }

    public ValidationDslBuilder greaterThanOrEquals(String rhsFieldName) {
        Errors errors = validationDslBuilder.getErrors();
        Comparable lhs = (Comparable) errors.getFieldValue(lhsFieldName);
        Comparable rhs = (Comparable) errors.getFieldValue(rhsFieldName);
        if (lhs != null && rhs != null && lhs.compareTo(rhs) < 0) {
            errors.rejectValue(lhsFieldName, String.format("requiredGreaterThanOrEquals.%s", rhsFieldName));
        }
        return validationDslBuilder;
    }

    public ValidationDslBuilder isOneOf(List values) {
        Errors errors = validationDslBuilder.getErrors();
        Object fieldValue = errors.getFieldValue(lhsFieldName);
        if (fieldValue != null && StringUtils.isNotBlank(String.valueOf(fieldValue)) && !values.contains(fieldValue)) {
            errors.rejectValue(lhsFieldName, INVALID_VALUE, new Object[]{fieldValue, lhsFieldName}, null);
        }
        return validationDslBuilder;
    }

    public ValidationDslBuilder isOneOf(Collection values) {
        return isOneOf(new ArrayList(values));
    }

    public ValidationDslBuilder isGreaterThan(String fieldName) {
        Errors errors = validationDslBuilder.getErrors();
        Object lhsValue = errors.getFieldValue(lhsFieldName);
        Object rhsValue = errors.getFieldValue(fieldName);
        if (lhsValue != null && rhsValue != null && ((Comparable) lhsValue).compareTo(rhsValue) <= 0) {
            errors.rejectValue(lhsFieldName, "requiredGreaterThan");
        }
        return validationDslBuilder;
    }

    public ValidationDslBuilder isGreaterThanZero() {
        Errors errors = validationDslBuilder.getErrors();
        Object lhsValue = errors.getFieldValue(lhsFieldName);
        if (lhsValue != null && ((Comparable) lhsValue).compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue(lhsFieldName, REQUIRED_GREATER_THAN_ZERO);
        }
        return validationDslBuilder;
    }

    public ValidationDslBuilder isPositive() {
        Errors errors = validationDslBuilder.getErrors();
        Object lhsValue = errors.getFieldValue(lhsFieldName);
        if (lhsValue != null && ((Comparable) lhsValue).compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue(lhsFieldName, REQUIRED_POSITIVE);
        }
        return validationDslBuilder;
    }
}
