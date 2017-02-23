package com.comviva.mfs.promotion.modules.user_management.validation;

import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.comviva.mfs.promotion.model.validation.Validatable;
import com.comviva.mfs.promotion.modules.user_management.model.UserManagementConfiguration;

/**
 * Created by tanmay.patel on 12/26/2016.
 */
public interface UserDetailsValidator extends Validatable {
    PropertyErrors validate(UserManagementConfiguration userManagementConfiguration);
}
