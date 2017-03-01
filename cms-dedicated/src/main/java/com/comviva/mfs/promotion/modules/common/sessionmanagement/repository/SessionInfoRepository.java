package com.comviva.mfs.promotion.modules.common.sessionmanagement.repository;

import com.comviva.mfs.promotion.modules.common.sessionmanagement.domain.SessionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SESSION_INFO repository.
 */
@Repository
public interface SessionInfoRepository extends JpaRepository<SessionInfo, String>{
    Optional<SessionInfo> findBySessionCode(String session_code);

    Optional<SessionInfo> findByAuthenticationCode(String authentication_code);

    Optional<SessionInfo> findByPaymentAppInstanceId(String payment_app_instance_id);
}
