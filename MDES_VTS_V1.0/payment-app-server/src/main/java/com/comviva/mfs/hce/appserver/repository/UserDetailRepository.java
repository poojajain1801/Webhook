package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import oracle.sql.TIMESTAMP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, String>{
    List<UserDetail> findByUserIdAndStatus(String userId, String stataus);
    List<UserDetail> findByClientWalletAccountId(String clientWalletAccountId);
    UserDetail findByUserId(String userId);

    @Query("SELECT u FROM UserDetail u " +
            "where u.createdOn between :fromDate and :toDate and " +
            "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
            "(CASE when (:status <> '-') then u.status else '-' end = :status )")
    List<UserDetail>findConsumerReport(@Param("fromDate")Date fromDate , @Param("toDate")Date toDate, @Param("userId")String userId,  @Param("status")String status);

    @Query("SELECT u FROM UserDetail u " +
            "where u.status <> 'N' and " +
            "(CASE when (:userId <> '-') then u.userId else '-' end = :userId )")
    List<UserDetail>findConsumerReportNoDate(@Param("userId")String userId);
}


