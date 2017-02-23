package com.comviva.mfs.promotion.modules.pack.service.contract;

import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;

/**
 * Created by sumit.das on 12/26/2016.
 */
public interface PackPolicyService {
    PackPolicy saveOrUpdate(PackPolicy packPolicy);
    PackPolicy getPackPolicyBasedOnType(String type);
}
