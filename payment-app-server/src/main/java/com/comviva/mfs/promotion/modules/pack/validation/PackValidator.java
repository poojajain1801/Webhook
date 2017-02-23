package com.comviva.mfs.promotion.modules.pack.validation;

import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.comviva.mfs.promotion.model.validation.Validatable;
import com.comviva.mfs.promotion.modules.pack.model.PackConfiguration;

/**
 * Created by sumit.das on 12/26/2016.
 */
public interface PackValidator extends Validatable {
    PropertyErrors validate(PackConfiguration packConfiguration);
}
