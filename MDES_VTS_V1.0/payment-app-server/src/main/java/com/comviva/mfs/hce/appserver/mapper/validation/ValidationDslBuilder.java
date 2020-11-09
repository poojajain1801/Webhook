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
package com.comviva.mfs.hce.appserver.mapper.validation;


import com.comviva.mfs.hce.appserver.mapper.error.PropertyErrors;

import java.util.function.Function;

public class ValidationDslBuilder {

    private ValidationContext validationContext;

    /**
     * ValidationDslBuilder constructor
     * @param validationContext validation context
     * */
    public ValidationDslBuilder(ValidationContext validationContext) {
        this.validationContext = validationContext;
    }


    /**
     * getErrors
     * @return PropertyErrors errorsList
     * */
    public PropertyErrors getErrors() {
        return validationContext.getErrors();
    }

    /**
     * withErrors
     * @param errors property errors
     * @return Validation Builder
     * */
    private ValidationDslBuilder withErrors(PropertyErrors errors) {
        return new ValidationDslBuilder(validationContext.withErrors(errors));
    }

    /**
     * validateRequired
     * @param fieldNames field names
     * @return Validation Builder
     * */
    public ValidationDslBuilder validateRequired(String... fieldNames) {
        PropertyErrors errors = DomainValidationUtils.validateRequired(validationContext.getErrors(), fieldNames);
        return this.withErrors(errors);
    }


    /**
     * validateFieldsNotPresent
     * @param fieldNames fieldNames
     * @return ValidationDslBuilder
     * */
    public ValidationDslBuilder validateFieldsNotPresent(String... fieldNames) {
        PropertyErrors errors = DomainValidationUtils.validateFieldsNotPresent(validationContext.getErrors(), fieldNames);
        return this.withErrors(errors);
    }


    /**
     * validateRequiredAnyOf
     * @param fieldNames fieldNames
     * @return ValidationDslBuilder
     * */
    public ValidationDslBuilder validateRequiredAnyOf(String... fieldNames) {
        PropertyErrors errors = DomainValidationUtils.validateRequiredAnyOf(validationContext.getErrors(), fieldNames);
        return this.withErrors(errors);
    }

    /**
     * validateShouldHaveOnlyOneOf
     * @param fieldNames fieldNames
     * @return ValidationDslBuilder
     * */
    public ValidationDslBuilder validateShouldHaveOnlyOneOf(String... fieldNames) {
        PropertyErrors errors = DomainValidationUtils.validateShouldHaveOnlyOneOf(validationContext.getErrors(), fieldNames);
        return this.withErrors(errors);
    }


    /**
     * validateThat
     * @param fieldName fieldNames
     * @return FieldValidation
     * */
    public FieldValidation validateThat(String fieldName) {
        return new FieldValidation(this, fieldName);
    }


    /**
     * validateThat
     * @param validationFunction validateFunction
     * @return FieldValidation
     * */
    public ValidationDslBuilder validateThat(Function<ValidationContext, PropertyErrors> validationFunction) {
        PropertyErrors errors = validationFunction.apply(validationContext);
        return this.withErrors(errors);
    }


    /**
     * validateNestedObject
     * @param fieldNames fieldNames
     * @return ValidationDslBuilder
     * */
    public ValidationDslBuilder validateNestedObject(String... fieldNames) {
        PropertyErrors errors = DomainValidationUtils.validateNestedObject(this.validationContext, fieldNames);
        return this.withErrors(errors);
    }


    /**
     * validateNestedObjectWithNewMetaData
     * @param fieldName fieldName
     * @param metadata details of object
     * @return ValidationDslBuilder
     * */
    public ValidationDslBuilder validateNestedObjectWithNewMetaData(String fieldName, Object metadata) {
        PropertyErrors errors = DomainValidationUtils.validateNestedObject(validationContext.withMetadata(metadata), fieldName);
        return this.withErrors(errors);
    }


    /**
     * validateCompositionObject
     * @param fieldNames fieldNames
     * @return validationDslBuilder validationBuilder
     * */
    public ValidationDslBuilder validateCompositionObject(String... fieldNames) {
        PropertyErrors errors = DomainValidationUtils.validateCompositionObject(this.validationContext, fieldNames);
        return this.withErrors(errors);
    }

    /**
     * validateRequiredIf
     * @param fieldNames fieldnames
     * @param conditionSatisfies true or false
     * @reutnr ValidationDslBuilder
     * */
    public ValidationDslBuilder validateRequiredIf(boolean conditionSatisfies, String... fieldNames) {
//        if(conditionSatisfies) {
//            return validateRequired(fieldNames);
//        }
//        return this;
        return conditionSatisfies?validateRequired(fieldNames):this;
    }
}
