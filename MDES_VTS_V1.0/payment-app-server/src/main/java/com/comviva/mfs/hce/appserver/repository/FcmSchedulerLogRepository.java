package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.FcmSchedulerLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FcmSchedulerLogRepository extends JpaRepository<FcmSchedulerLog, String> {

    @Modifying(clearAutomatically = true)
    @Query("update FcmSchedulerLog fs set fs.status =:status where fs.rnsRegistrationId=:rnsRegistrationId" )
    void updateRecord(@Param("rnsRegistrationId") String rnsRegistrationId, @Param("status") String status);

    @Query("select distinct f.rnsRegistrationId from FcmSchedulerLog f where status = 'I'")
    List<String> fetchRnsIdWithStatusI();
}
