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
package com.comviva.mfs.promotion.model.validation;

import com.comviva.mfs.promotion.model.error.PropertyErrors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

public class DomainValidationUtils {

    private DomainValidationUtils() {
    }

    public static class ErrorCodes {
        public static final String REQUIRED = "required";
        public static final String REQUIRED_ONLY_ONE_OF = "requiredOnlyOneOf";
        public static final String INVALID_VALUE = "invalidValue";
        public static final String REQUIRED_GREATER_THAN_ZERO = "requiredGreaterThanZero";
        public static final String REQUIRED_POSITIVE = "requiredPositive";
        public static final String DUPLICATE = "duplicate";
        public static final String CONCURRENT_MODIFICATION = "concurrentModification";
        public static final String VERSION_MISMATCH = "versionMismatch";
        public static final Object NOT_FOUND = "notFound";

        private ErrorCodes() {
        }
    }

    public static PropertyErrors validateRequired(PropertyErrors errors, String... fields) {
        for (String field : fields) {
            Object fieldValue = errors.getFieldValue(field);
            // Performance fix. Without this condition it call toString of class which can be expensive
            if (fieldValue == null) {
                errors.rejectValue(field, ErrorCodes.REQUIRED);
            } else if (fieldValue instanceof Collection) {
                validateRequiredCollection(errors, field);
            } else if (fieldValue instanceof String) {
                rejectIfEmptyOrWhitespace(errors, field, ErrorCodes.REQUIRED);
            }
        }
        return errors;
    }

    public static PropertyErrors validateRequiredAnyOf(PropertyErrors errors, String... fields) {
        boolean hasNonBlankValue = Arrays.asList(fields).stream().anyMatch(field -> {
            Object value = errors.getFieldValue(field);
            return !isBlank(value);
        });
        if (!hasNonBlankValue) {
            String fieldsString = String.join("Or", Arrays.asList(fields).stream().map(WordUtils::capitalize).toArray(String[]::new));

            errors.rejectValue(null, String.format("%s.%s", ErrorCodes.REQUIRED, WordUtils.uncapitalize(fieldsString)));
        }
        return errors;
    }

    private static PropertyErrors validateEachInNestedCollection(ValidationContext validationContext, String field) {
        final PropertyErrors errors = validationContext.getErrors();
        Collection validatables = firstNonNull((Collection) errors.getFieldValue(field), Collections.emptyList());
        Object[] validatablesArray = validatables.toArray();
        PropertyErrors newErrors = IntStream.range(0, validatables.size()).mapToObj(index -> {
            String subPath = String.format("%s[%d]", field, index);
            Validatable validatable = (Validatable) validatablesArray[index];
            PropertyErrors propertyErrors = new PropertyErrors(errors.getTarget(), errors.getObjectName());
            propertyErrors.setNestedPath(errors.getNestedPath());
            ValidationContext validationContextWithNewErrors = validationContext.withErrors(propertyErrors);
            return validateNestedObject(validationContextWithNewErrors, subPath, validatable);
        }).reduce(errors, (allErrors, fieldError) -> {
            allErrors.addAllErrors(fieldError);
            return allErrors;
        });
        return newErrors;
    }

    public static PropertyErrors validateNestedObject(ValidationContext validationContext, String... fields) {
        PropertyErrors newErrors = validationContext.getErrors();
        for (String field : fields) {
            if (isCollection(newErrors.getFieldType(field))) {
                newErrors = validateEachInNestedCollection(validationContext.withErrors(newErrors), field);
            } else {
                newErrors = validateNestedObject(validationContext.withErrors(newErrors), field, (Validatable) newErrors.getFieldValue(field));
            }
        }
        return newErrors;
    }

    public static PropertyErrors validateCompositionObject(ValidationContext validationContext, String... fields) {
        PropertyErrors errors = validateNestedObject(validationContext, fields);
        //TODO: Look for better ways to achieve this. FYI: not providing sub path doesn't work
        return validationContext.withErrors(errors).withoutErrorPathsForFields(fields).getErrors();
    }

    private static PropertyErrors validateRequiredCollection(PropertyErrors errors, String... fields) {
        for (String field : fields) {
            Collection collection = (Collection) errors.getFieldValue(field);
            if (isEmpty(collection)) {
                errors.rejectValue(field, ErrorCodes.REQUIRED);
            }
        }
        return errors;
    }

    public static PropertyErrors validateFieldsNotPresent(PropertyErrors errors, String... fields) {
        for (String field : fields) {
            Object value = errors.getFieldValue(field);
            if (value != null) {
                errors.rejectValue(field, ErrorCodes.INVALID_VALUE, new Object[]{value, field}, null);
            }
        }
        return errors;
    }

    public static PropertyErrors validateShouldHaveOnlyOneOf(PropertyErrors errors, String... fields) {
        long numberOfFieldsWithValue = Arrays.asList(fields).stream().filter(field -> errors.getFieldValue(field) != null).count();
        if (numberOfFieldsWithValue > 1) {
            String fieldsString = String.join("Or", Arrays.asList(fields).stream().map(WordUtils::capitalize).toArray(String[]::new));
            errors.rejectValue(null, String.format("%s.%s", ErrorCodes.REQUIRED_ONLY_ONE_OF, WordUtils.uncapitalize(fieldsString)));
        }
        return errors;
    }

    private static PropertyErrors validateNestedObject(ValidationContext validationContext, String subPath, Validatable validatable) {
        PropertyErrors newErrors = validationContext.getErrors();
        if (validatable == null) {
            return newErrors;
        }
        try {
            newErrors.pushNestedPath(subPath);
            newErrors = validatable.validate(validationContext);
        } finally {
            newErrors.popNestedPath();
        }
        return newErrors;
    }

    private static boolean isCollection(Class<?> fieldType) {
        return fieldType != null && Collection.class.isAssignableFrom(fieldType);
    }

    private static boolean isBlank(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof String ? StringUtils.isBlank((String) value) : false;
    }
}
