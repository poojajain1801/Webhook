package com.comviva.mfs.promotion.modules.pack.repository;

import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by sumit.das on 12/26/2016.
 */
public interface PackPolicyRepository extends JpaRepository<PackPolicy, String> {
    Optional<PackPolicy> findByType(String type);
}
