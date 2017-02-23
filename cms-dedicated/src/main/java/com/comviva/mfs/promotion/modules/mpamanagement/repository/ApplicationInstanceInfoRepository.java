package com.comviva.mfs.promotion.modules.mpamanagement.repository;

import com.comviva.mfs.promotion.modules.mpamanagement.domain.ApplicationInstanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationInstanceInfoRepository extends JpaRepository<ApplicationInstanceInfo, String>{
    Optional<ApplicationInstanceInfo> findByPaymentAppInstId(String paymentAppInstId);

}
