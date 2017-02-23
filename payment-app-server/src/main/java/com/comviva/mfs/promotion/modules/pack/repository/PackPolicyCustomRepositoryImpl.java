package com.comviva.mfs.promotion.modules.pack.repository;

import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by sumit.das on 12/27/2016.
 */
@Repository
public class PackPolicyCustomRepositoryImpl implements PackPolicyCustomRepository {
    @Autowired
    private  EntityManager entityManager;

    @Transactional
    @Override
    public PackPolicy updateOrSave(PackPolicy packPolicy) {
        if (isNotEmpty(packPolicy.getId())) {
            entityManager.merge(packPolicy);
        } else {
            entityManager.persist(packPolicy);
        }
        entityManager.flush();
        return findOneBasedOnPolicyType(packPolicy);
    }

    private PackPolicy findOneBasedOnPolicyType(PackPolicy packPolicy){
        List<PackPolicy> packPolicyList = entityManager
                .createQuery("Select policy FROM PackPolicy policy where policy.type = :type")
                .setParameter("type", packPolicy.getType())
                .getResultList();
        return isEmpty(packPolicyList) ? null : packPolicyList.get(0);
    }
}
