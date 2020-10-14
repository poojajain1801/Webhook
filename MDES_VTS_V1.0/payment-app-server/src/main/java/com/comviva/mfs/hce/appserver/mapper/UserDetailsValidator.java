package com.comviva.mfs.hce.appserver.mapper;

import com.comviva.mfs.hce.appserver.mapper.error.PropertyErrors;
import com.comviva.mfs.hce.appserver.mapper.validation.Validatable;


/**
 * Created by tanmay.patel on 12/26/2016.
 */
public interface UserDetailsValidator extends Validatable {
    PropertyErrors validate(UserManagementConfiguration userManagementConfiguration);
}
