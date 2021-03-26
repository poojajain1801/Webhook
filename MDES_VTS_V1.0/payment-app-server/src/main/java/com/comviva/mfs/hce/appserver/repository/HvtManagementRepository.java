package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.HvtManagement;
import com.comviva.mfs.hce.appserver.model.HvtManagementPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface HvtManagementRepository extends JpaRepository<HvtManagement, HvtManagementPK> {

    @Modifying(clearAutomatically = true)
    @Query("update HvtManagement hm set hm.hvtLimit =:hvtLimit, hm.isHvtSupported=:isHvtSupported " +
            "where hm.id.paymentAppId=:paymentAppId and hm.id.requestId=:requestId")
    void update(@Param("requestId") String requestId, @Param("paymentAppId") String paymentAppId,
                @Param("isHvtSupported") String isHvtSupported, @Param("hvtLimit") String hvtLimit);

    @Query("select hm from HvtManagement hm where hm.id.paymentAppId=:paymentAppId")
    HvtManagement findByPaymentAppId(@Param("paymentAppId")String paymentAppId);
}
