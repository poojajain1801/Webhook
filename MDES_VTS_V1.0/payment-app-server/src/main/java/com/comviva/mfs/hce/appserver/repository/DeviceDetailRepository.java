/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface DeviceDetailRepository extends JpaRepository<DeviceInfo, String>{

    /**
     * find
     * @param clientDeviceId clientDeviceId
     * @return list of devices
     * */
    @Query("Select distinct mp from DeviceInfo mp where mp.clientDeviceId =:clientDeviceId")
    List<DeviceInfo> find(@Param("clientDeviceId") String clientDeviceId);

    /**
     * findByImei
     * @param imei imei
     * @return list of devices
     * */
    List<DeviceInfo> findByImei(String imei);

    /**
     * findByStatus
     * @param status status
     * @return list of devices
     * */
    List<DeviceInfo> findByStatus(String status);

    /**
     * findByPaymentAppInstanceId
     * @param payment_app_instance_id paymentAppInstanceId
     * @return list of devices
     * */
    Optional<DeviceInfo> findByPaymentAppInstanceId(String payment_app_instance_id);

    /**
     * findByClientDeviceId
     * @param clientDeviceId clientDeviceId
     * @return li
     * */
    Optional<DeviceInfo> findByClientDeviceId(String clientDeviceId);

    /**
     * findByImeiAndStatus
     * @param imei imei
     * @param status status
     * @return list of devices
     * */
    List<DeviceInfo> findByImeiAndStatus(String imei, String status);

    /**
     * findByClientDeviceIdAndStatus
     * @param clientDeviceId clientDeviceId
     * @param status status
     * @return list of devices
     * */
    List<DeviceInfo> findByClientDeviceIdAndStatus(String clientDeviceId, String status);

    /**
     * findByClientWalletAccountId
     * @param clientWalletAccountId clientWalletAccountId
     * @return list of devices
     * */
    @Query("Select d from DeviceInfo d where d.userDetail.clientWalletAccountId=:clientWalletAccountId")
    List<DeviceInfo> findByClientWalletAccountId(@Param("clientWalletAccountId") String clientWalletAccountId);


    /**
     * findByClientWalletAccountIdAndStatus
     * @param clientWalletAccountId clientWalletAccountId
     * @param status status
     * @return list of devices
     * */
    @Query("Select d from DeviceInfo d where d.userDetail.clientWalletAccountId=:clientWalletAccountId and d.status<>:status")
    List<DeviceInfo> findByClientWalletAccountIdAndStatus(@Param("clientWalletAccountId") String clientWalletAccountId,
                                                          @Param("status") String status);


    /**
     * findDeviceDetails
     * @param clientWalletAccountId clientWalletAccountId
     * @param clientDeviceId clientDeviceId
     * @param status status
     * @return list of devices
     * */
    @Query("Select d from DeviceInfo d where d.clientDeviceId =:clientDeviceId and d.userDetail.clientWalletAccountId=:clientWalletAccountId " +
            "and d.status=:status")
    List<DeviceInfo> findDeviceDetails(@Param("clientDeviceId") String clientDeviceId, @Param("clientWalletAccountId") String clientWalletAccountId,
                                       @Param("status") String status);


    /**
     * findDeviceDetailsWithIMEI
     * @param imei imei
     * @param status status
     * @return list of devices
     * */
    @Query("Select d from DeviceInfo d where d.imei =:imei and d.userDetail.userId=:userId and d.status=:status")
    DeviceInfo findDeviceDetailsWithIMEI(@Param("imei") String imei, @Param("userId") String userId,@Param("status") String status);

    /**
     * findDeviceDetailsWithIMEIAndUserId
     * @param imei imei
     * @param userId userId
     * @return list of devices
     * */
    @Query("Select d from DeviceInfo d where d.imei =:imei and d.userDetail.userId=:userId")
    List<DeviceInfo> findDeviceDetailsWithIMEIAndUserId(@Param("imei") String imei, @Param("userId") String userId);

    /**
     * findDeviceReport
     * @param fromDate fromDate
     * @param toDate toDate
     * @param userId userId
     * @param imei imei
     * @param userStatus userStatus
     * @param deviceStatus deviceStatus
     * @return list of device Reports
     * */
    @Query("SELECT u.userId, u.createdOn, u.status, d.imei, d.deviceName, d.deviceModel, d.osVersion, " +
            "d.status, d.createdOn FROM DeviceInfo d JOIN d.userDetail u " +
        "where d.createdOn between :fromDate and :toDate and " +
        "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
        "(CASE when (:imei <> '-') then d.imei else '-' end = :imei ) and " +
        "(CASE when (:userStatus <> '-') then u.status else '-' end = :userStatus ) and " +
        "(CASE when (:deviceStatus <> '-') then d.status else '-' end = :deviceStatus )")
    List<Object[]> findDeviceReport(@Param("fromDate")Date fromDate , @Param("toDate")Date toDate, @Param("userId")String userId,
                                    @Param("imei")String imei, @Param("userStatus")String userStatus,
                                    @Param("deviceStatus")String deviceStatus);


    /**
     * findDeviceReport
     * @param userId userId
     * @param imei imei
     * @param userStatus userStatus
     * @param deviceStatus deviceStatus
     * @return list of device Reports
     * */
    @Query("SELECT u.userId, u.createdOn, u.status, d.imei, d.deviceName, d.deviceModel, d.osVersion, d.status, " +
            "d.createdOn FROM DeviceInfo d JOIN d.userDetail u " +
            "where d.status <> 'N' and " +
            "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
            "(CASE when (:imei <> '-') then d.imei else '-' end = :imei ) and " +
            "(CASE when (:userStatus <> '-') then u.status else '-' end = :userStatus ) and " +
            "(CASE when (:deviceStatus <> '-') then d.status else '-' end = :deviceStatus )")
    List<Object[]> findDeviceReportNoDate(@Param("userId")String userId, @Param("imei")String imei, @Param("userStatus")String userStatus,
                                          @Param("deviceStatus")String deviceStatus);

}
