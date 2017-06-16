package com.comviva.mfs.promotion.model.error;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.AbstractBindingResult;

public class PropertyErrors extends AbstractBindingResult {
    private final Object target;

    public PropertyErrors(Object target, String objectName) {
        super(objectName);
        this.target = target;
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    protected Object getActualFieldValue(String field) {
        return PropertyAccessorFactory.forBeanPropertyAccess(target).getPropertyValue(field);
    }
}
