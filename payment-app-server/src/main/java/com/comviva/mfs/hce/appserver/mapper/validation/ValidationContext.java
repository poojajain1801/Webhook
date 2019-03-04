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
package com.comviva.mfs.hce.appserver.mapper.validation;

import com.comviva.mfs.hce.appserver.mapper.error.PropertyErrors;
import org.springframework.validation.FieldError;

import java.util.Arrays;

public class ValidationContext {
    private final PropertyErrors errors;
    private final Object metadata;

    public ValidationContext(PropertyErrors errors, Object metadata) {
        this.errors = errors;
        this.metadata = metadata;
    }

    public PropertyErrors getErrors() {
        return errors;
    }

    public Object getMetadata() {
        return metadata;
    }

    public ValidationContext withoutErrorPathsForFields(String[] compositeFields) {
        String nestedPath = errors.getNestedPath();
        PropertyErrors newErrors = new PropertyErrors(errors.getTarget(), errors.getObjectName());
        Arrays.asList(nestedPath.split("\\.")).forEach(newErrors::pushNestedPath);
        errors.getAllErrors().forEach(objectError -> {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                String fieldNameWithoutGivenPath = getPathWithoutCompositionFields(compositeFields, fieldError.getField());
                String[] errorCodesWithoutGivenPath = Arrays.asList(fieldError.getCodes()).stream().map(s -> getPathWithoutCompositionFields(compositeFields, s)).toArray(String[]::new);
                newErrors.addError(new FieldError(fieldError.getObjectName(),
                        fieldNameWithoutGivenPath, fieldError.getRejectedValue(), false,
                        errorCodesWithoutGivenPath, fieldError.getArguments(), fieldError.getDefaultMessage())
                );
            } else {
                newErrors.addError(objectError);
            }
        });
        return this.withErrors(newErrors);
    }

    private String getPathWithoutCompositionFields(String[] fields, String valueToReplace) {
        return Arrays.asList(fields).stream()
                .reduce(valueToReplace, (errorField, fieldName) -> errorField.replace(fieldName + ".", ""));
    }

    public ValidationContext withErrors(PropertyErrors errors) {
        return new ValidationContext(errors, metadata);
    }

    public ValidationContext withMetadata(Object newMetaData) {
        return new ValidationContext(errors, newMetaData);
    }
}
