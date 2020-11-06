/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.repository;


import com.comviva.mfs.hce.appserver.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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
            "where u.status <> 'N' and u.userId = :userId")
    List<UserDetail>findConsumerReportNoDate(@Param("userId")String userId);
}


