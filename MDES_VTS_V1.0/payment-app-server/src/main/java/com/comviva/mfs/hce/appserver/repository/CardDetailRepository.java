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

import com.comviva.mfs.hce.appserver.model.CardDetails;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface CardDetailRepository extends JpaRepository<CardDetails, String> {


    /**
     * findByPanUniqueReferenceAndClientDeviceId
     * @param clientDeviceId clientDeviceId
     * @param panUniqueReference panUniqueReference
     * @param initiated initiated
     * @return list of CardDetails
     * */
    @Query("Select vc from CardDetails vc where vc.deviceInfo.clientDeviceId=:clientDeviceId and " +
            "vc.panUniqueReference=:panUniqueReference and vc.status in (:initiated) order by vc.createdOn DESC")
    List<CardDetails> findByPanUniqueReferenceAndClientDeviceId(@Param("panUniqueReference") String panUniqueReference,
                                                                @Param("clientDeviceId") String clientDeviceId,
                                                                @Param("initiated") String initiated);

    /**
     * findByPanUniqueReference
     * @param panUniqueReference panUniqueId
     * @return list of card details
     * */
    List<CardDetails> findByPanUniqueReference(String panUniqueReference);

    /**
     * findByStatus
     * @param Status status
     * @return list of card details
     * */
    List<CardDetails> findByStatus(String Status);


    /**
     * findByVisaProvisionTokenId
     * @param visaProvisionTokenId visaTokenId
     * @return list of card details
     * */
    List<CardDetails> findByVisaProvisionTokenId(String visaProvisionTokenId);

    /**
     * findCardDetailsByIdentifier
     * @param cardIdentifier cardIdentifier
     * @param clientWalletAccountId clientWalletAccountId
     * @param clientDeviceId clientDeviceId
     * @param active active
     * @param suspend suspend
     * @return list of card details
     * */
    @Query("Select vc from CardDetails vc where vc.cardIdentifier =:cardIdentifier and vc.deviceInfo.userDetail.clientWalletAccountId=:clientWalletAccountId " +
            "and vc.deviceInfo.clientDeviceId=:clientDeviceId and vc.status in(:active,:suspend)")
    List<CardDetails> findCardDetailsByIdentifier(@Param("cardIdentifier") String cardIdentifier, @Param("clientWalletAccountId") String clientWalletAccountId,
                                                  @Param("clientDeviceId") String clientDeviceId, @Param("active") String active,
                                                  @Param("suspend") String suspend);


    /**
     * findByMasterTokenUniqueReference
     * @param masterTokenUniqueReference masterTokenUniqueReference
     * @return CardDetails
     * */
    Optional<CardDetails> findByMasterTokenUniqueReference(String masterTokenUniqueReference);

    /**
     * findByMasterPaymentAppInstanceIdAndMasterTokenUniqueReference
     * @param masterPaymentAppInstanceId masterPaymentAppInstanceId
     * @param masterTokenUniqueReference masterTokenUniqueReference
     * @return CardDetails
     * */
    Optional<CardDetails> findByMasterPaymentAppInstanceIdAndMasterTokenUniqueReference(String masterPaymentAppInstanceId,
                                                                                        String masterTokenUniqueReference);

    /**
     * getCardList
     * @param userId userId
     * @param suspend suspend
     * @param active active
     * @param pageable pageable
     * @param status status
     * @return list of cardDetails
     * */
    @Query("Select vc from CardDetails vc where vc.deviceInfo.userDetail.userId=:userId and vc.deviceInfo.status=:deviceStatus " +
            "and vc.status in (:active,:suspend) order by vc.createdOn DESC")
    List<CardDetails> getCardList(@Param("userId") String userId, @Param("deviceStatus") String status, @Param("active") String active,
                                  @Param("suspend") String suspend, Pageable pageable);


    /**
     * GETCARDLIST
     * @param userId userId
     * @param suspend suspend
     * @param active active
     * @param status deviceStatus
     * @return list of cardDetails
     * */
    @Query("Select vc from CardDetails vc where vc.deviceInfo.userDetail.userId=:userId and vc.deviceInfo.status=:deviceStatus " +
            "and vc.status in (:active,:suspend) order by vc.createdOn DESC")
    List<CardDetails> getCardList(@Param("userId") String userId, @Param("deviceStatus") String status, @Param("active") String active,
                                  @Param("suspend") String suspend);


    /**
     * getNCardList
     * @param page page
     * @param size size
     * @param userId userId
     * @param suspend suspend
     * @param active active
     * @param deviceStatus deviceStatus
     * @return list of cardDetails
     * */
    default List<CardDetails> getNCardList(String userId, String deviceStatus, String active, String suspend, int page, int size) {
        if (size == 0) {
            return getCardList(userId, deviceStatus, active, suspend);
        } else {
            return getCardList(userId, deviceStatus, active, suspend, new PageRequest(page, size));
        }

    }


    /**
     * updateCardDetails
     * @param status status
     * @param clientDeviceId clientDeviceId
     * */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update CardDetails vc set vc.status =:status where vc.deviceInfo.clientDeviceId=:clientDeviceId and vc.status <> 'N'" )
    void updateCardDetails(@Param("clientDeviceId") String clientDeviceId, @Param("status") String status);


    /**
     * findUserDeviceCardReport
     * @param deviceStatus deviceStatus
     * @param fromDate fromDate
     * @param imei imei
     * @param toDate toDate
     * @param userId userId
     * @param userStatus userStatus
     * @return list of objects
     * */
    @Query("SELECT u.userId, u.createdOn, u.status, d.imei, d.deviceName, d.deviceModel, d.osVersion, d.status, d.createdOn, " +
            "c.cardSuffix, c.tokenSuffix, c.createdOn, c.status, c.replenishOn, c.masterPaymentAppInstanceId, c.cardType, " +
            "c.masterTokenUniqueReference, c.visaProvisionTokenId, u.modifiedOn FROM CardDetails c JOIN c.deviceInfo d JOIN d.userDetail u " +
            "where c.createdOn between :fromDate and :toDate and " +
            "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
            "(CASE when (:imei <> '-') then d.imei else '-' end = :imei ) and " +
            "(CASE when (:userStatus <> '-') then u.status else '-' end = :userStatus ) and " +
            "(CASE when (:deviceStatus <> '-') then d.status else '-' end = :deviceStatus )")
    List<Object[]> findUserDeviceCardReport(@Param("fromDate")Date fromDate , @Param("toDate")Date toDate, @Param("userId")String userId,
                                            @Param("imei")String imei, @Param("userStatus")String userStatus, @Param("deviceStatus")String deviceStatus);


    /**
     * findUserDeviceCardReportWithoutDate
     * @param deviceStatus deviceStatus
     * @param imei imei
     * @param userId userId
     * @param userStatus userStatus
     * @return list of objects
     * */
    @Query("SELECT u.userId, u.createdOn, u.status, d.imei, d.deviceName, d.deviceModel, d.osVersion, d.status, d.createdOn, c.cardSuffix, " +
            "c.tokenSuffix, c.createdOn, c.status, c.replenishOn, c.masterPaymentAppInstanceId, c.cardType, c.masterTokenUniqueReference, " +
            "c.visaProvisionTokenId, u.modifiedOn FROM CardDetails c JOIN c.deviceInfo d JOIN d.userDetail u " +
            "where d.status <> 'N' and c.status <> 'N' and " +
            "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
            "(CASE when (:imei <> '-') then d.imei else '-' end = :imei ) and " +
            "(CASE when (:userStatus <> '-') then u.status else '-' end = :userStatus ) and " +
            "(CASE when (:deviceStatus <> '-') then d.status else '-' end = :deviceStatus )")
    List<Object[]> findUserDeviceCardReportWithoutDate(@Param("userId")String userId, @Param("imei")String imei, @Param("userStatus")String userStatus,
                                                       @Param("deviceStatus")String deviceStatus);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update CardDetails vc set vc.repersoStatus =:status where vc.visaProvisionTokenId=:vprovisionId")
    void updateRepersoStatus(@Param("vprovisionId")String vprovisionId, @Param("status")String status);

}

