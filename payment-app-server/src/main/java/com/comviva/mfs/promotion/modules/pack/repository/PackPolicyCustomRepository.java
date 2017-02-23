package com.comviva.mfs.promotion.modules.pack.repository;

import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import org.springframework.data.repository.Repository;

/**
 * Created by sumit.das on 12/27/2016.
 */
public interface PackPolicyCustomRepository extends Repository<PackPolicy, String> {
    /**
     * This method accept pack policy as a parameter and check whether
     * policy is present or not. If not present, then store in the db
     * else it will merge the existing policy and return back the stored policy
     * @param packPolicy
     * @return
     */
    PackPolicy updateOrSave(PackPolicy packPolicy);
}
