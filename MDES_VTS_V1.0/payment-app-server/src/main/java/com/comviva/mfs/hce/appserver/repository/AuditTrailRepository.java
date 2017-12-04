package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.AuditTrail;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by shadab.ali on 13-09-2017.
 */
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, String> {

}
