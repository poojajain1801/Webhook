package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface DeviceDetailRepository extends JpaRepository<DeviceInfo, String>{

    @Query("Select distinct mp from DeviceInfo mp where mp.clientDeviceId =:clientDeviceId")
        //Map<String,Object> find(@Param("userName") String userName);
    List<DeviceInfo> find(@Param("clientDeviceId") String clientDeviceId);
    List<DeviceInfo> findByImei(String imei);
    List<DeviceInfo> findByStatus(String status);
    Optional<DeviceInfo> findByPaymentAppInstanceId(String payment_app_instance_id);
    Optional<DeviceInfo> findByClientDeviceId(String clientDeviceId);
    List<DeviceInfo> findByImeiAndStatus(String imei, String status);
    List<DeviceInfo> findByClientDeviceIdAndStatus(String clientDeviceId, String status);

    @Query("Select d from DeviceInfo d where d.userDetail.clientWalletAccountId=:clientWalletAccountId")
    List<DeviceInfo> findByClientWalletAccountId(@Param("clientWalletAccountId") String clientWalletAccountId);

    @Query("Select d from DeviceInfo d where d.userDetail.clientWalletAccountId=:clientWalletAccountId and d.status<>:status")
    List<DeviceInfo> findByClientWalletAccountIdAndStatus(@Param("clientWalletAccountId") String clientWalletAccountId,@Param("status") String status);

    @Query("Select d from DeviceInfo d where d.clientDeviceId =:clientDeviceId and d.userDetail.clientWalletAccountId=:clientWalletAccountId and d.status=:status")
    List<DeviceInfo> findDeviceDetails(@Param("clientDeviceId") String clientDeviceId, @Param("clientWalletAccountId") String clientWalletAccountId,@Param("status") String status);

    @Query("Select d from DeviceInfo d where d.imei =:imei and d.userDetail.userId=:userId and d.status=:status")
    DeviceInfo findDeviceDetailsWithIMEI(@Param("imei") String imei, @Param("userId") String userId,@Param("status") String status);

    @Query("Select d from DeviceInfo d where d.imei =:imei and d.userDetail.userId=:userId")
    List<DeviceInfo> findDeviceDetailsWithIMEIAndUserId(@Param("imei") String imei, @Param("userId") String userId);

    @Query("SELECT u.userId, u.createdOn, u.status, d.imei, d.deviceName, d.deviceModel, d.osVersion, d.status, d.createdOn FROM DeviceInfo d JOIN d.userDetail u " +
        "where d.createdOn between :fromDate and :toDate and " +
        "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
        "(CASE when (:imei <> '-') then d.imei else '-' end = :imei ) and " +
        "(CASE when (:userStatus <> '-') then u.status else '-' end = :userStatus ) and " +
        "(CASE when (:deviceStatus <> '-') then d.status else '-' end = :deviceStatus )")
    List<Object[]> findDeviceReport(@Param("fromDate")Date fromDate , @Param("toDate")Date toDate, @Param("userId")String userId, @Param("imei")String imei, @Param("userStatus")String userStatus, @Param("deviceStatus")String deviceStatus);

    @Query("SELECT u.userId, u.createdOn, u.status, d.imei, d.deviceName, d.deviceModel, d.osVersion, d.status, d.createdOn FROM DeviceInfo d JOIN d.userDetail u " +
            "where d.status <> 'N' and " +
            "(CASE when (:userId <> '-') then u.userId else '-' end = :userId ) and " +
            "(CASE when (:imei <> '-') then d.imei else '-' end = :imei ) and " +
            "(CASE when (:userStatus <> '-') then u.status else '-' end = :userStatus ) and " +
            "(CASE when (:deviceStatus <> '-') then d.status else '-' end = :deviceStatus )")
    List<Object[]> findDeviceReportNoDate(@Param("userId")String userId, @Param("imei")String imei, @Param("userStatus")String userStatus, @Param("deviceStatus")String deviceStatus);


    @Query("select distinct d.rnsRegistrationId from DeviceInfo d where d.status = 'Y'")
    List<String> fetchRnsRegistrationId();

}
