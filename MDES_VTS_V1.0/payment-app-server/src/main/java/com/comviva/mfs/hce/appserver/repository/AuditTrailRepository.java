package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.AuditTrail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by shadab.ali on 13-09-2017.
 */
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, String> {

    @Query("SELECT a FROM AuditTrail a " +
            "where a.createdOn between :fromDate and :toDate")
    List<AuditTrail> findAuditTrailReport(@Param("fromDate") Date fromDate , @Param("toDate")Date toDate);

}
