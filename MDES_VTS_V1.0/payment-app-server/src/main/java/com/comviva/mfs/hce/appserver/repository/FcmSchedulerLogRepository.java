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




    /**
     * select rns_registration_id, max(created_on) as created_on
     * from device_info
     * where status = 'Y' and rns_registration_id is not null
     * group by rns_registration_id
     * order by created_on
     * limit 10 offset 190;
     * Query(value = "select rnsRegistrationId, max(d.createdOn) as createdOn from DeviceInfo where status = 'Y'
     * group by rnsRegistrationId order by createdOn limit :limit offset :offset", nativeQuery = true)
     */
    @Query(value = "select rns_registration_id, max(created_on) as created_on from fcm_scheduler_log where status = 'I' " +
            "group by rns_registration_id order by created_on limit :limit offset :offset", nativeQuery = true)
    List<Object[]> fetchRnsIdWithStatusI(@Param("limit")int limit, @Param("offset")int offset);


    @Query(value = "rns_registration_id, max(created_on) as created_on from fcm_scheduler_log where status = 'I' " +
            "group by rns_registration_id order by created_on OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY",
            nativeQuery = true)
    List<Object[]> fetchRnsRegistrationIdOracle(@Param("limit")int limit, @Param("offset")int offset);


    @Query("select count(distinct d.rnsRegistrationId) from FcmSchedulerLog d where d.status = 'I'")
    int countOfRnsIds();


}
